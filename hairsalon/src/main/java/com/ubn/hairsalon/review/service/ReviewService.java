package com.ubn.hairsalon.review.service;

import com.ubn.hairsalon.config.file.FileService;
import com.ubn.hairsalon.config.util.TimeUtil;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.repository.MemberRepository;
import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.reserve.repository.ReserveRepository;
import com.ubn.hairsalon.review.dto.ReviewFormDto;
import com.ubn.hairsalon.review.dto.ReviewImgDto;
import com.ubn.hairsalon.review.dto.ReviewSearchDto;
import com.ubn.hairsalon.review.dto.ReviewThumbnailDto;
import com.ubn.hairsalon.review.entity.Review;
import com.ubn.hairsalon.review.entity.ReviewImg;
import com.ubn.hairsalon.review.repository.ReviewImgRepository;
import com.ubn.hairsalon.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImgService reviewImgService;
    private final ReserveRepository reserveRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    @Transactional
    public Long createReview(ReviewFormDto reviewFormDto, List<MultipartFile> reviewFileList, Reserve reserve) throws Exception {
        // 리뷰 등록
        Review review = reviewFormDto.review(reserve);
        reviewRepository.save(review);
        reserveRepository.updateReviewYnById("Y", reserve.getId()); // 해당 예약에 리뷰 등록 처리

        // 리뷰에 첨부된 사진 등록
        for(int i=0; i<reviewFileList.size(); i++) {
            ReviewImg reviewImg = new ReviewImg();
            reviewImg.setReview(review);
            if(i == 0) {
                reviewImg.setRepImgYn("Y");
            } else {
                reviewImg.setRepImgYn("N");
            }
            reviewImgService.saveReviewImg(reviewImg, reviewFileList.get(i));
        }

        return review.getId();
    }

    // 리뷰 썸네일(검색, 페이징)
    @Transactional(readOnly = true)
    public Page<ReviewThumbnailDto> getReviewThumbnailPage(ReviewSearchDto reviewSearchDto, Pageable pageable) {
        Page<ReviewThumbnailDto> reviewThumbnailPage = reviewRepository.getReviewThumbnailPage(reviewSearchDto, pageable);
        for (ReviewThumbnailDto reviewThumbnailDto : reviewThumbnailPage) {
            String writtenTime = TimeUtil.getElapsedTime(reviewThumbnailDto.getCreatedDate());
            reviewThumbnailDto.setWrittenTime(writtenTime);
        }
        return reviewThumbnailPage;
    }

    // 리뷰 상세
    @Transactional(readOnly = true)
    public ReviewFormDto getReviewDetail(Long reserveId) {

        Review findReviewId = reviewRepository.findByReserveId(reserveId);
        long reviewId = findReviewId.getId();

        List<ReviewImg>  reviewImgList = reviewImgRepository.findByReviewIdOrderByImgIdAsc(reviewId);
        List<ReviewImgDto> reviewImgDtoList = new ArrayList<>();
        for(ReviewImg reviewImg : reviewImgList) {
            ReviewImgDto reviewImgDto = ReviewImgDto.of(reviewImg);
            reviewImgDtoList.add(reviewImgDto);
        }

        Review review = reviewRepository.findById(reviewId).orElseThrow(EntityNotFoundException::new);
        ReviewFormDto reviewFormDto = ReviewFormDto.of(review);
        reviewFormDto.setReviewImgDtoList(reviewImgDtoList);
        return reviewFormDto;
    }

    // 리뷰 수정
    @Transactional
    public Long setUpdateReview(ReviewFormDto reviewFormDto, List<MultipartFile> reviewImgFileList) throws Exception {
        Review review = reviewRepository.findById(reviewFormDto.getReviewId()).orElseThrow(EntityNotFoundException::new);
        review.updateReview(reviewFormDto);

        List<Long> reviewImgIds = reviewFormDto.getReviewImgIds();
        for(int i=0; i<reviewImgFileList.size(); i++) {
            reviewImgService.updateReviewImg(reviewImgIds.get(i), reviewImgFileList.get(i));
        }
        return review.getId();
    }

    @Transactional(readOnly = true)
    public boolean validateReview(Long reviewId, String email) {
        // 로그인한 사용자와 같은지 검증..
        Member member = memberRepository.findByEmail(email);
        Review review = reviewRepository.findById(reviewId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = review.getReserve().getMember();

        if(!StringUtils.equals(member.getEmail(), savedMember.getEmail())) {
            return false;
        }

        return true;
    }

    @Transactional
    public void setDeleteReview(Long reviewId) throws Exception {
        Review review = reviewRepository.findById(reviewId).orElseThrow(EntityNotFoundException::new);
        Reserve reserve = review.getReserve();

        // review와 join된 reviewImg도 함께 삭제
        List<ReviewImg> reviewImgList = review.getReviewImgList();
        for (ReviewImg reviewImg : reviewImgList) {
            if (!StringUtils.isEmpty(reviewImg.getImgUrl())) {
                fileService.deleteFile(reviewImg.getImgUrl());
            }
            reviewImgService.deleteReviewImg(reviewImg.getImgId());
        }
        reviewRepository.delete(review);
        // Reserve 엔티티의 reviewYn 변경
        reserve.setReviewYn("N");
        reserveRepository.save(reserve);
    }

}

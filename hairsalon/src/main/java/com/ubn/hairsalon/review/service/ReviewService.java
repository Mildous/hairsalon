package com.ubn.hairsalon.review.service;

import com.ubn.hairsalon.config.util.TimeUtil;
import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.reserve.repository.ReserveRepository;
import com.ubn.hairsalon.review.dto.ReviewFormDto;
import com.ubn.hairsalon.review.dto.ReviewSearchDto;
import com.ubn.hairsalon.review.dto.ReviewThumbnailDto;
import com.ubn.hairsalon.review.entity.Review;
import com.ubn.hairsalon.review.entity.ReviewImg;
import com.ubn.hairsalon.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImgService reviewImgService;
    private final ReserveRepository reserveRepository;

    public Long createReview(ReviewFormDto reviewFormDto, List<MultipartFile> reviewFileList, Reserve reserve) throws Exception {
        // 리뷰 등록
        Review review = reviewFormDto.review(reserve);
        System.err.println("reviewRepository.save(review)");
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
            System.err.println("reviewImgService.saveReviewImg(reviewImg, reviewFileList.get(i))");
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



}

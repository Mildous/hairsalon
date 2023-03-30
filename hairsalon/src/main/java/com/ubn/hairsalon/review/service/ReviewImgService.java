package com.ubn.hairsalon.review.service;

import com.ubn.hairsalon.config.file.FileService;
import com.ubn.hairsalon.review.entity.ReviewImg;
import com.ubn.hairsalon.review.repository.ReviewImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewImgService {

    @Value("${reviewImgLocation}")
    private String reviewImgLocation;

    private final ReviewImgRepository reviewImgRepository;
    private final FileService fileService;

    @Transactional
    public void saveReviewImg(ReviewImg reviewImg, MultipartFile reviewImgFile) throws Exception {
        String oriImgName = reviewImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // file Upload..
        if(!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(reviewImgLocation, oriImgName, reviewImgFile.getBytes());
            imgUrl = "/images/review/" + imgName;
        }

        // save review image's info
        reviewImg.updateReviewImg(oriImgName, imgName, imgUrl);
        reviewImgRepository.save(reviewImg);
    }

    @Transactional
    public void updateReviewImg(Long imgId, MultipartFile reviewImgFile) throws Exception {
        if(reviewImgFile != null && !reviewImgFile.isEmpty()) {
            ReviewImg savedImg = reviewImgRepository.findById(imgId).orElseThrow(EntityNotFoundException::new);

            // 기존 이미지 삭제..
            if(!StringUtils.isEmpty(savedImg.getImgName())) {
                fileService.deleteFile(reviewImgLocation + "/" + savedImg.getImgName());
            }

            String oriImgName = reviewImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(reviewImgLocation, oriImgName, reviewImgFile.getBytes());
            String imgUrl = "/images/review/" + imgName;
            savedImg.updateReviewImg(oriImgName, imgName, imgUrl);
        }
    }



    @Transactional
    public void deleteReviewImg(Long imgId) throws Exception {
        ReviewImg savedImg = reviewImgRepository.findById(imgId).orElseThrow(EntityNotFoundException::new);

        // 기존 이미지 삭제..
        if (!StringUtils.isEmpty(savedImg.getImgName())) {
            fileService.deleteFile(reviewImgLocation + "/" + savedImg.getImgName());
        }

        // reviewImg와 review의 연관관계를 끊음
        savedImg.setReview(null);

        reviewImgRepository.delete(savedImg);
    }

}

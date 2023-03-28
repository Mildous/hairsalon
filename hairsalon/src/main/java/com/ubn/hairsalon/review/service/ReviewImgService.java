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

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewImgService {

    @Value("${reviewImgLocation}")
    private String reviewImgLocation;

    private final ReviewImgRepository reviewImgRepository;
    private final FileService fileService;

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
}

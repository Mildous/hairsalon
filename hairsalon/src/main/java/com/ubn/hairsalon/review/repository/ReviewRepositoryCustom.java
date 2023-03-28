package com.ubn.hairsalon.review.repository;

import com.ubn.hairsalon.review.dto.ReviewSearchDto;
import com.ubn.hairsalon.review.dto.ReviewThumbnailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {
    Page<ReviewThumbnailDto> getReviewThumbnailPage(ReviewSearchDto reviewSearchDto, Pageable pageable);
}

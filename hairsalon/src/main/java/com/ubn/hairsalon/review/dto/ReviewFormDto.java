package com.ubn.hairsalon.review.dto;

import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.review.entity.Review;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ReviewFormDto {

    private Long reviewId;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 작성해주세요.")
    private String content;

    @NotNull(message = "별점을 입력해주세요.")
    private Integer rating;

    private List<ReviewImgDto> reviewImgDtoList = new ArrayList<>();

    private List<Long> reviewImgIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    public Review review(Reserve reserve) {
        Review review = new Review();
        review.setReserve(reserve);
        review.setTitle(getTitle());
        review.setContent(getContent());
        review.setRating(getRating());
        return review;
    }

    public static ReviewFormDto of(Review review) {
        return modelMapper.map(review, ReviewFormDto.class);
    }

}

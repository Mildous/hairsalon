package com.ubn.hairsalon.review.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.ubn.hairsalon.member.constant.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ReviewThumbnailDto {

    private Long id;
    private String title;
    private String content;
    private Gender gender;

    private String typeName;
    private String imgUrl;
    private Integer rating;

    private LocalDateTime createdDate;
    private String writtenTime;

    @QueryProjection
    public ReviewThumbnailDto(Long id, String title, String content, Gender gender, String typeName, String imgUrl, Integer rating, LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.gender = gender;
        this.typeName = typeName;
        this.imgUrl = imgUrl;
        this.rating = rating;
        this.createdDate = createdDate;
    }

}

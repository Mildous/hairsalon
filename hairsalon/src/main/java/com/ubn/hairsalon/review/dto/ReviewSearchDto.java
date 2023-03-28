package com.ubn.hairsalon.review.dto;

import com.ubn.hairsalon.member.constant.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewSearchDto {

    private String type;        // searchType
    private Gender gender;      // searchGender
    private String by;          // searchByReview.. content, title
    private String query;       // searchQuery.. content, title

}

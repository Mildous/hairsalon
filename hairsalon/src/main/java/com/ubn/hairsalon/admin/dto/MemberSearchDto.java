package com.ubn.hairsalon.admin.dto;

import com.ubn.hairsalon.member.constant.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberSearchDto {

    private String date;                    // searchBirthMonth
    private Gender gender;                  // searchGender
    private String by;                      // searchByMember.. memberName, phone
    private String query;                   // searchQuery.. 검색어..

}

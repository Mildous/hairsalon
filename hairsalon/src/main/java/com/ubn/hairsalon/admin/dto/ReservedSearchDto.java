package com.ubn.hairsalon.admin.dto;

import com.ubn.hairsalon.member.constant.Gender;
import com.ubn.hairsalon.reserve.constant.ReserveStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReservedSearchDto {

    private String date;                // searchDateType..
    private ReserveStatus status;       // reserveStatus 예약 상태..
    private Gender gender;              // searchGender
    private String by;                  // searchByMember.. memberName, phone
    private String query;               // searchQuery.. 검색어..

}

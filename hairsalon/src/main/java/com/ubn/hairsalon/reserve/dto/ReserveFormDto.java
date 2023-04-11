package com.ubn.hairsalon.reserve.dto;

import com.ubn.hairsalon.reserve.entity.Reserve;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter @Setter
public class ReserveFormDto {

    private Long id;

    private Long memberId;

    @Positive(message = "시술명은 필수입니다.")
    private Long typeId;

    @NotBlank(message = "예약일은 필수입니다.")
    private String rsvDate;

    @NotBlank(message = "예약시간은 필수입니다.")
    private String rsvStartTime;

    private String rsvEndTime;

    private String typeName;

    private static ModelMapper modelMapper = new ModelMapper();

    public static ReserveFormDto of(Reserve reserve) {
        return modelMapper.map(reserve, ReserveFormDto.class);
    }
}

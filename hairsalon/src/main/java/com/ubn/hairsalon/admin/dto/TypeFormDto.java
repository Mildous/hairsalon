package com.ubn.hairsalon.admin.dto;

import com.ubn.hairsalon.admin.entity.Type;
import com.ubn.hairsalon.member.constant.Gender;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.*;

@Getter @Setter
public class TypeFormDto {

    private Long typeId;

    @NotBlank(message = "시술 이름은 필수 입력사항입니다.")
    private String typeName;

    @NotNull(message = "시술 성별은 필수 입력사항입니다.")
    private Gender typeGender;

    @NotNull(message = "시술 소요시간은 필수 입력사항입니다.")
    private int takeMinutes;

    private static ModelMapper modelMapper = new ModelMapper();

    public static TypeFormDto of(Type type) {
        return modelMapper.map(type, TypeFormDto.class);
    }
}

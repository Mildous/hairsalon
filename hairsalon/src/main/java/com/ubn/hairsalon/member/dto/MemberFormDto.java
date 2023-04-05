package com.ubn.hairsalon.member.dto;

import com.ubn.hairsalon.member.constant.BirthMax;
import com.ubn.hairsalon.member.constant.Gender;
import com.ubn.hairsalon.member.constant.OAuth2Provider;
import com.ubn.hairsalon.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter @Setter
public class MemberFormDto {

    private Long id;

    @NotBlank(message = "이름은 필수 입력사항입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력사항입니다.")
    @Email(message = "올바르지 않은 이메일 형식입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력사항입니다.")
    @Length(min=8, max=20, message = "비밀번호는 8자 이상, 20자 이하로 입력해주세요.")
    private String password;

    // ^01(?:0|1|[6-9])[.-]?(\d{3}|\d{4})[.-]?(\d{4})$
    @NotBlank(message = "전화번호는 필수 입력사항입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", message = "전화번호는 10 ~ 11 자리의 숫자만 입력 가능합니다.")
    private String phone;

    @NotNull(message = "생년월일은 필수 입력사항입니다.")
    @BirthMax(message = "2002년 12월 31일 이전 출생자만 가입 가능합니다.")
    @Past(message = "생년월일은 과거 날짜만 입력 가능합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @NotNull(message = "성별은 필수 입력사항입니다.")
    private Gender gender;

    private OAuth2Provider oAuth2Provider;

    private Long kakaoId;

    private static ModelMapper modelMapper = new ModelMapper();

    public static MemberFormDto of(Member member) {
        return modelMapper.map(member, MemberFormDto.class);
    }
}

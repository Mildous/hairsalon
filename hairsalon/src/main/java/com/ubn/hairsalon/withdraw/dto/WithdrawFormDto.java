package com.ubn.hairsalon.withdraw.dto;

import com.ubn.hairsalon.member.constant.Gender;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Getter @Setter
public class WithdrawFormDto {

    private Long memberId;

    private String email;

    private Gender gender;

    private LocalDate birth;

    @NotEmpty(message = "탈퇴사유는 필수 입력사항입니다.")
    @Length(min=10, max=300, message = "탈퇴사유는 10자 이상, 300자 이하로 입력해주세요.")
    private String reason;

}

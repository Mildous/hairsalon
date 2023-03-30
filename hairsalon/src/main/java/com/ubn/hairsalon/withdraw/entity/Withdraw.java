package com.ubn.hairsalon.withdraw.entity;

import com.ubn.hairsalon.config.entity.BaseEntity;
import com.ubn.hairsalon.member.constant.Gender;
import com.ubn.hairsalon.withdraw.dto.WithdrawFormDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "withdraw")
@Data @EqualsAndHashCode(callSuper=true)
public class Withdraw extends BaseEntity {

    @Id
    @Column(name = "withdraw_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birth;

    private String reason;

    public static Withdraw createWithdrawMember(WithdrawFormDto withdrawFormDto) {
        Withdraw withdraw = new Withdraw();
        withdraw.setEmail(withdrawFormDto.getEmail());
        withdraw.setGender(withdrawFormDto.getGender());
        withdraw.setBirth(LocalDate.parse(withdrawFormDto.getBirth()));
        withdraw.setReason(withdrawFormDto.getReason());
        return withdraw;
    }
}

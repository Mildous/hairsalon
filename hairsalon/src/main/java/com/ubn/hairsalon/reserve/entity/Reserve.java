package com.ubn.hairsalon.reserve.entity;

import com.ubn.hairsalon.admin.entity.Type;
import com.ubn.hairsalon.config.entity.BaseEntity;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.reserve.constant.ReserveStatus;
import com.ubn.hairsalon.reserve.constant.ServiceStatus;
import com.ubn.hairsalon.reserve.dto.ReserveFormDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reserve")
@Getter @Setter
public class Reserve extends BaseEntity {

    @Id
    @Column(name = "rsv_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Type type;

    @Column(nullable = false)
    private LocalDate rsvDate;

    @Column(nullable = false)
    private LocalTime rsvStartTime;

    @Column(nullable = false)
    private LocalTime rsvEndTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReserveStatus reserveStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceStatus serviceStatus;

    // 리뷰 작성 여부
    @Column(nullable = false)
    private String reviewYn;


    public static Reserve createReserve(Member member, Type type, ReserveFormDto reserveFormDto) {
        Reserve reserve = new Reserve();
        reserve.setMember(member);
        reserve.setType(type);
        reserve.setRsvDate(LocalDate.parse(reserveFormDto.getRsvDate()));
        reserve.setRsvStartTime(LocalTime.parse(reserveFormDto.getRsvStartTime()));
        reserve.setRsvEndTime(LocalTime.parse(reserveFormDto.getRsvEndTime()));
        reserve.setReserveStatus(ReserveStatus.WAITING);
        reserve.setServiceStatus(ServiceStatus.WAITING);
        reserve.setReviewYn("N");
        return reserve;
    }

    public void updateProfileReserved(Member member, Type type, ReserveFormDto reserveFormDto) {
        this.member = member;
        this.type = type;
        this.rsvDate = LocalDate.parse(reserveFormDto.getRsvDate());
        this.rsvStartTime = LocalTime.parse(reserveFormDto.getRsvStartTime());
        this.rsvEndTime = LocalTime.parse(reserveFormDto.getRsvEndTime());
    }

}

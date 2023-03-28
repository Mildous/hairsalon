package com.ubn.hairsalon.review.entity;

import com.ubn.hairsalon.config.entity.BaseEntity;
import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.review.dto.ReviewFormDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "review")
@Getter @Setter
public class Review extends BaseEntity {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "reserve_id")
    private Reserve reserve;

    private String title;

    @Lob
    private String content;

    private int rating;

}

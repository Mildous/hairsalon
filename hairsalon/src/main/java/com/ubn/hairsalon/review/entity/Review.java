package com.ubn.hairsalon.review.entity;

import com.ubn.hairsalon.config.entity.BaseEntity;
import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.review.dto.ReviewFormDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
@Getter @Setter
public class Review extends BaseEntity {

    /**
     * Review Entity
     *
     * @id      : 리뷰 시퀀스
     * @reserve : 예약 엔티티(1:1 Mapping)
     * @title   : 제목
     * @content : 내용
     * @rating  : 별점 점수
     */

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_id")
    private Reserve reserve;

    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImg> reviewImgList = new ArrayList<>();

    private String title;

    @Lob
    private String content;

    private int rating;

    public void updateReview(ReviewFormDto reviewFormDto) {
        this.title = reviewFormDto.getTitle();
        this.content = reviewFormDto.getContent();
        this.rating = reviewFormDto.getRating();
    }

}

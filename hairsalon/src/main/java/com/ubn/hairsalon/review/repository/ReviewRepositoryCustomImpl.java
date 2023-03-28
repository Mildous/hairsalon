package com.ubn.hairsalon.review.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ubn.hairsalon.member.constant.Gender;
import com.ubn.hairsalon.review.dto.QReviewThumbnailDto;
import com.ubn.hairsalon.review.dto.ReviewSearchDto;
import com.ubn.hairsalon.review.dto.ReviewThumbnailDto;
import com.ubn.hairsalon.review.entity.QReview;
import com.ubn.hairsalon.review.entity.QReviewImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private JPAQueryFactory queryFactory;

    private ReviewRepositoryCustomImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em); }

    private BooleanExpression searchTypeNameEq(String searchTypeName) {
        return searchTypeName == null ? null : QReview.review.reserve.type.typeName.eq(searchTypeName);
    }

    private BooleanExpression searchGenderEq(Gender searchGender) {
        return searchGender == null ? null : QReview.review.reserve.member.gender.eq(searchGender);
    }

    private BooleanExpression searchByReviewLike(String searchBy, String searchQuery) {
        if(StringUtils.equals("title", searchBy)) {
            return QReview.review.title.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("content", searchBy)) {
            return QReview.review.content.like("%" + searchQuery + "%");
        }
        return null;
    }

    @Override
    public Page<ReviewThumbnailDto> getReviewThumbnailPage(ReviewSearchDto reviewSearchDto, Pageable pageable) {
        QReview review = QReview.review;
        QReviewImg reviewImg = QReviewImg.reviewImg;

        List<ReviewThumbnailDto> content = queryFactory
                .select(
                        new QReviewThumbnailDto(
                                review.id,
                                review.title,
                                review.content,
                                review.reserve.member.gender,
                                review.reserve.type.typeName,
                                reviewImg.imgUrl,
                                review.rating,
                                review.createdDate
                        )
                )
                .from(reviewImg)
                .join(reviewImg.review, review)
                .where(reviewImg.repImgYn.eq("Y"))
                .where(searchTypeNameEq(reviewSearchDto.getType()))
                .where(searchGenderEq(reviewSearchDto.getGender()))
                .where(searchByReviewLike(reviewSearchDto.getBy(), reviewSearchDto.getQuery()))
                .orderBy(review.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(Wildcard.count)
                .from(reviewImg)
                .join(reviewImg.review, review)
                .where(reviewImg.repImgYn.eq("Y"))
                .where(searchTypeNameEq(reviewSearchDto.getType()))
                .where(searchGenderEq(reviewSearchDto.getGender()))
                .where(searchByReviewLike(reviewSearchDto.getBy(), reviewSearchDto.getQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}

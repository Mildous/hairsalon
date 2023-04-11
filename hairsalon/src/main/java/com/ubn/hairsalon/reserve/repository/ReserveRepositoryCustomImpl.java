package com.ubn.hairsalon.reserve.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ubn.hairsalon.admin.dto.ReservedSearchDto;
import com.ubn.hairsalon.member.constant.Gender;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.reserve.constant.ReserveStatus;
import com.ubn.hairsalon.reserve.entity.QReserve;
import com.ubn.hairsalon.reserve.entity.Reserve;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class ReserveRepositoryCustomImpl implements ReserveRepositoryCustom {

    private JPAQueryFactory queryFactory;

    private ReserveRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression rsvDateAfter(String searchDate) {
        LocalDate today = LocalDate.now();
        LocalDate after = LocalDate.now();

        if(StringUtils.equals("all", searchDate) || searchDate == null) {
            return null;
        } else if(StringUtils.equals("1d", searchDate)) {
            after = today.plusDays(1);
        } else if(StringUtils.equals("1w", searchDate)) {
            after = today.plusDays(7);
        } else if(StringUtils.equals("1m", searchDate)) {
            after = today.plusMonths(1);
        } else if(StringUtils.equals("6m", searchDate)) {
            after = today.plusMonths(6);
        }
        return QReserve.reserve.rsvDate.between(today, after);
    }

    private BooleanExpression searchReserveStatusEq(ReserveStatus searchReserveStatus) {
        return searchReserveStatus == null ? null : QReserve.reserve.reserveStatus.eq(searchReserveStatus);
    }

    private BooleanExpression searchGenderEq(Gender searchGender) {
        return searchGender == null ? null : QReserve.reserve.member.gender.eq(searchGender);
    }

    private BooleanExpression searchByMemberLike(String searchBy, String searchQuery) {
        if(StringUtils.equals("name", searchBy)) {
            return QReserve.reserve.member.name.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("phone", searchBy)) {
            return QReserve.reserve.member.phone.like("%" + searchQuery + "%");
        }

        return null;
    }


    @Override
    public Page<Reserve> getReservedAdminPage(ReservedSearchDto reservedSearchDto, Pageable pageable) {
        List<Reserve> reserves = queryFactory
                .selectFrom(QReserve.reserve)
                .where(rsvDateAfter(reservedSearchDto.getDate()),
                        searchReserveStatusEq(reservedSearchDto.getStatus()),
                        searchGenderEq(reservedSearchDto.getGender()),
                        searchByMemberLike((reservedSearchDto.getBy()), reservedSearchDto.getQuery()))
                .orderBy(QReserve.reserve.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(Wildcard.count).from(QReserve.reserve)
                .where(rsvDateAfter(reservedSearchDto.getDate()),
                        searchReserveStatusEq(reservedSearchDto.getStatus()),
                        searchGenderEq(reservedSearchDto.getGender()),
                        searchByMemberLike((reservedSearchDto.getBy()), reservedSearchDto.getQuery()))
                .fetchOne();
        return new PageImpl<>(reserves, pageable, total);
    }

    private BooleanExpression searchMemberIdEq(Long id) {
        return id == null ? null : QReserve.reserve.member.id.eq(id);
    }

    @Override
    public Page<Reserve> getReservedProfilePage(Member member, Pageable pageable) {
        List<Reserve> reserves = queryFactory
                .selectFrom(QReserve.reserve)
                .where(searchMemberIdEq(member.getId()))
                .orderBy(QReserve.reserve.modifiedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = queryFactory.select(Wildcard.count).from(QReserve.reserve)
                .where(searchMemberIdEq(member.getId()))
                .fetchOne();
        return new PageImpl<>(reserves, pageable, total);
    }


}

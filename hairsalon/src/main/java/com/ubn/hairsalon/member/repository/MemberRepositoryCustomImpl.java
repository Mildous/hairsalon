package com.ubn.hairsalon.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ubn.hairsalon.admin.dto.MemberSearchDto;
import com.ubn.hairsalon.member.constant.Gender;
import com.ubn.hairsalon.member.constant.Role;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.entity.QMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private JPAQueryFactory queryFactory;

    private MemberRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression monthDateType(String searchDate) {
        if(StringUtils.equals("all", searchDate) || searchDate == null) {
            return null;
        }
        try {
            int month = Integer.parseInt(searchDate);
            if (month < 1 || month > 12) {
                return null;
            }
            return QMember.member.birth.month().eq(month);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private BooleanExpression searchGenderEq(Gender searchGender) {
        return searchGender == null ? null : QMember.member.gender.eq(searchGender);
    }

    private BooleanExpression searchByMemberLike(String searchBy, String searchQuery) {
        if(StringUtils.equals("name", searchBy)) {
            return QMember.member.name.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("phone", searchBy)) {
            return QMember.member.phone.like("%" + searchQuery + "%");
        }

        return null;
    }

    @Override
    public Page<Member> getMembersAdminPage(MemberSearchDto memberSearchDto, Pageable pageable) {
        List<Member> members = queryFactory
                .selectFrom(QMember.member)
                .where(monthDateType(memberSearchDto.getDate()),
                        searchGenderEq(memberSearchDto.getGender()),
                        searchByMemberLike((memberSearchDto.getBy()), memberSearchDto.getQuery()),
                        QMember.member.role.eq(Role.USER))
                .orderBy(QMember.member.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = queryFactory.select(Wildcard.count).from(QMember.member)
                .where(monthDateType(memberSearchDto.getDate()),
                        searchGenderEq(memberSearchDto.getGender()),
                        searchByMemberLike((memberSearchDto.getBy()), memberSearchDto.getQuery()),
                        QMember.member.role.eq(Role.USER))
                .fetchOne();
        return new PageImpl<>(members, pageable, total);
    }
}

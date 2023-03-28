package com.ubn.hairsalon.member.repository;

import com.ubn.hairsalon.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member>, MemberRepositoryCustom {
    Member findByEmail(String email);

    @Modifying
    @Query("update Member m set m.memo = :memo where m.id = :id")
    int updateMemoById(@Param("memo") String memo, @Param("id") Long id);
}

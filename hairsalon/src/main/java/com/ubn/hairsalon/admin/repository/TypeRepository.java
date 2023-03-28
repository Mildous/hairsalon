package com.ubn.hairsalon.admin.repository;

import com.ubn.hairsalon.admin.entity.Type;
import com.ubn.hairsalon.member.constant.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TypeRepository extends JpaRepository<Type, Long> {
    List<Type> findAllByOrderByIdDesc();

    // 로그인한 회원의 성별을 조회하여 해당하는 타입을 반환
    @Query("select t from Type t where t.typeGender = :gender order by t.takeMinutes asc")
    List<Type> findMembersType(@Param("gender")Gender gender);

}

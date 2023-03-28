package com.ubn.hairsalon.member.repository;

import com.ubn.hairsalon.admin.dto.MemberSearchDto;
import com.ubn.hairsalon.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<Member> getMembersAdminPage(MemberSearchDto memberSearchDto, Pageable pageable);
}

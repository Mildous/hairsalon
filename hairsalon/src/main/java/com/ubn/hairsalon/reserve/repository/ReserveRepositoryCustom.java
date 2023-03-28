package com.ubn.hairsalon.reserve.repository;

import com.ubn.hairsalon.admin.dto.ReservedSearchDto;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.reserve.entity.Reserve;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReserveRepositoryCustom {
    Page<Reserve> getReservedAdminPage(ReservedSearchDto reservedSearchDto, Pageable pageable);
    Page<Reserve> getReservedProfilePage(Member member, Pageable pageable);
}

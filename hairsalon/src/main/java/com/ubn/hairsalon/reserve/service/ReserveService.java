package com.ubn.hairsalon.reserve.service;

import com.ubn.hairsalon.admin.dto.ReservedSearchDto;
import com.ubn.hairsalon.admin.entity.Type;
import com.ubn.hairsalon.admin.repository.TypeRepository;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.repository.MemberRepository;
import com.ubn.hairsalon.reserve.constant.ReserveStatus;
import com.ubn.hairsalon.reserve.constant.ServiceStatus;
import com.ubn.hairsalon.reserve.dto.ReserveFormDto;
import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.reserve.repository.ReserveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;
    private final MemberRepository memberRepository;
    private final TypeRepository typeRepository;

    // 현재 로그인한 회원의 성별을 가져와서 해당되는 시술 종류들을 찾아서 반환함
    @Transactional(readOnly = true)
    public List<Type> findMembersType(String email) {
        // 현재 로그인한 회원의 엔티티 조회
        Member member = memberRepository.findByEmail(email);
        // 회원의 성별에 해당하는 시술 타입들을 조회하여 반환
        List<Type> types = typeRepository.findMembersType(member.getGender());
        return types;
    }

    public Reserve saveReserve(Reserve reserve) {
        return reserveRepository.save(reserve);
    }

    // 해당 날짜에 예약되어 있는 시간들을 Map 객체에 담아서 반환함
    @Transactional(readOnly = true)
    public List<String> getReservedTime(String rsvDate) throws Exception {
        List<Reserve> reserves = reserveRepository.findReservesByRsvDateOrderByIdAsc(LocalDate.parse(rsvDate));
        List<String> list = new ArrayList<>();
        for(Reserve reserve : reserves) {
            list.add(reserve.getRsvStartTime().toString() + '-' + reserve.getRsvEndTime().toString());
        }
        return list;
    }

    // 예약 목록 (검색, 페이징)
    @Transactional(readOnly = true)
    public Page<Reserve> getReservedAdminPage(ReservedSearchDto reservedSearchDto, Pageable pageable) {
        return reserveRepository.getReservedAdminPage(reservedSearchDto, pageable);
    }

    // 예약 목록 (캘린더형)
    @Transactional(readOnly = true)
    public List<Reserve> getReservedAll() {
        List<Reserve> reserves = reserveRepository.findAll();
        return reserves;
    }

    // 사용자의 예약 수정
    @Transactional
    public Long updateProfileReserved(Member member, Type type, ReserveFormDto reserveFormDto) {
        Reserve reserve = reserveRepository.findById(reserveFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        reserve.updateProfileReserved(member, type, reserveFormDto);
        return reserve.getId();
    }

    // 사용자의 예약 취소
    public void deleteProfileReserved(Long id) {
        Reserve reserve = reserveRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        reserveRepository.delete(reserve);
    }

    // 사용자의 예약 목록
    @Transactional(readOnly = true)
    public Page<Reserve> getReservedProfilePage(Member member, Pageable pageable) {
        return reserveRepository.getReservedProfilePage(member, pageable);
    }

    // 예약 상세정보
    @Transactional(readOnly = true)
    public ReserveFormDto getReservedDetail(Long rsvId) {
        Reserve reserve = reserveRepository.findById(rsvId).orElseThrow(EntityNotFoundException::new);
        ReserveFormDto reserveFormDto = ReserveFormDto.of(reserve);
        return reserveFormDto;
    }

    // 관리자의 예약 상태 변경
    @Transactional
    public Long reserveStatus(Long id, ReserveStatus reserveStatus) {
        int result = reserveRepository.updateReserveStatusById(reserveStatus, id);
        if(result > 0) {
            Reserve reserve = reserveRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            return reserve.getId();
        } else {
            return null;
        }
    }

    // 예약 상태
    @Transactional
    public Long serviceStatus(Long id, ServiceStatus serviceStatus) {
        int result = reserveRepository.updateServiceStatusById(serviceStatus, id);
        if(result > 0) {
            Reserve reserve = reserveRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            return reserve.getId();
        } else {
            return null;
        }
    }

}

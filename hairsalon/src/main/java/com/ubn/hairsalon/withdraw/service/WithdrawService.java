package com.ubn.hairsalon.withdraw.service;

import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.repository.MemberRepository;
import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.withdraw.entity.Withdraw;
import com.ubn.hairsalon.withdraw.repository.WithdrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WithdrawService {

    private final WithdrawRepository withdrawRepository;

    public Withdraw saveWithdrawMember(Withdraw withdraw) {
        return withdrawRepository.save(withdraw);
    }

    // 탈퇴 회원 목록
    @Transactional(readOnly = true)
    public Page<Withdraw> getWithdrawMembers(Pageable pageable) {
        return withdrawRepository.findAll(pageable);
    }
}

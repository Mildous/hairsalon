package com.ubn.hairsalon.member.service;

import com.ubn.hairsalon.admin.dto.MemberSearchDto;
import com.ubn.hairsalon.member.dto.MemberFormDto;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.repository.MemberRepository;
import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.reserve.service.ReserveService;
import com.ubn.hairsalon.withdraw.dto.WithdrawFormDto;
import com.ubn.hairsalon.withdraw.entity.Withdraw;
import com.ubn.hairsalon.withdraw.repository.WithdrawRepository;
import com.ubn.hairsalon.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final WithdrawService withdrawService;
    private final ReserveService reserveService;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if(member == null) {
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

    // Update..
    @Transactional(readOnly = true)
    public MemberFormDto getProfile(Long id) throws UsernameNotFoundException {
        Member member = memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        MemberFormDto memberFormDto = MemberFormDto.of(member);
        return memberFormDto;
    }

    @Transactional
    public Long updateProfile(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) throws Exception {
        Member member = memberRepository.findById(memberFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        member.updateMember(memberFormDto, passwordEncoder);
        return member.getId();
    }

    // 회원 목록(검색, 페이징)
    @Transactional(readOnly = true)
    public Page<Member> getMembersAdminPage(MemberSearchDto memberSearchDto, Pageable pageable) {
        return memberRepository.getMembersAdminPage(memberSearchDto, pageable);
    }

    @Transactional
    public Long updateMemberMemo(String memo, Long id) {
        int result = memberRepository.updateMemoById(memo, id);
        if(result > 0) {
            Member member = memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            return member.getId();
        } else {
            return null;
        }
    }

    @Transactional
    public void withdrawMember(Long memberId, String reason) {
        Member member = memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);
        WithdrawFormDto withdrawFormDto = new WithdrawFormDto();
        withdrawFormDto.setGender(member.getGender());
        withdrawFormDto.setBirth(String.valueOf(member.getBirth()));
        withdrawFormDto.setReason(reason);
        Withdraw withdraw = Withdraw.createWithdrawMember(withdrawFormDto);

        member.withdraw(reason);
        withdrawService.saveWithdrawMember(withdraw);

        // Remove Personal Data from Reserves
        for (Reserve reserve : member.getReserves()) {
            reserve.removePersonalData();
        }
    }

}

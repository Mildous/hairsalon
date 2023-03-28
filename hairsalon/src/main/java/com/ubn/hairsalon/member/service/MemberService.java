package com.ubn.hairsalon.member.service;

import com.ubn.hairsalon.admin.dto.MemberSearchDto;
import com.ubn.hairsalon.member.dto.MemberFormDto;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.repository.MemberRepository;
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

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
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

}

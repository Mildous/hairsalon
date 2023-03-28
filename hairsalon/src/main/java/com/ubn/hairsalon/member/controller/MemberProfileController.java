package com.ubn.hairsalon.member.controller;

import com.ubn.hairsalon.member.dto.MemberFormDto;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.repository.MemberRepository;
import com.ubn.hairsalon.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/members/profile")
@RequiredArgsConstructor
public class MemberProfileController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    // 회원정보 보기
    @GetMapping(value = "/info")
    public String profileMember(Principal principal, Model model) {
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);
        model.addAttribute("now", "profile");
        model.addAttribute("profile", member);
        return "members/profile/info";
    }

    // 회원정보 수정
    @GetMapping(value = "/edit")
    public String profileUpdate(Principal principal, Model model) {
        String email = principal.getName();
        Long id = memberRepository.findByEmail(email).getId();
        MemberFormDto memberFormDto = memberService.getProfile(id);
        model.addAttribute("now", "profile");
        model.addAttribute("memberFormDto", memberFormDto);
        return "members/joinForm";
    }

    // 수정 처리
    @PostMapping(value = "/edit/{id}")
    public String profileUpdate(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            return "members/joinForm";
        }
        try {
            memberService.updateProfile(memberFormDto, passwordEncoder);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "members/joinForm";
        }

        return "redirect:/members/profile/info";
    }

}

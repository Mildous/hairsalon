package com.ubn.hairsalon.member.controller;

import com.ubn.hairsalon.member.constant.OAuth2Provider;
import com.ubn.hairsalon.member.dto.MemberFormDto;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/new")
    public String memberForm(Model model, @RequestParam(value = "kakaoId", required = false) String kakaoId) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        if (kakaoId != null) {
            model.addAttribute("kakaoId", kakaoId);
        }
        model.addAttribute("now", "");
        return "members/joinForm";
    }

    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model, @RequestParam(value = "kakaoId", required = false) Long kakaoId) {
        if (bindingResult.hasErrors()) {
            return "members/joinForm";
        }

        if (kakaoId != null) {
            memberFormDto.setOAuth2Provider(OAuth2Provider.KAKAO);
            memberFormDto.setKakaoId(kakaoId);
        } else {
            memberFormDto.setOAuth2Provider(OAuth2Provider.LOCAL);
            memberFormDto.setKakaoId(null);
        }
        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "members/joinForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember(Model model) {
        model.addAttribute("now5", "login");
        return "members/login";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "members/login";
    }

    @GetMapping(value = "/login/kakao/error")
    public String kakaoLoginError(Model model) {
        model.addAttribute("loginErrorMsg", "카카오톡 로그인에 실패하였습니다.");
        return "members/login";
    }


}


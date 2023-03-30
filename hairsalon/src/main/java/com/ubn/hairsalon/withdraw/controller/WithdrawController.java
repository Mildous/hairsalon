package com.ubn.hairsalon.withdraw.controller;

import com.ubn.hairsalon.member.constant.Role;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.repository.MemberRepository;
import com.ubn.hairsalon.reserve.repository.ReserveRepository;
import com.ubn.hairsalon.withdraw.dto.WithdrawFormDto;
import com.ubn.hairsalon.withdraw.entity.Withdraw;
import com.ubn.hairsalon.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/members/withdraw")
@RequiredArgsConstructor
public class WithdrawController {

    private final MemberRepository memberRepository;
    private final WithdrawService withdrawService;
    private final ReserveRepository reserveRepository;

    // 회원 탈퇴 폼
    @GetMapping(value = "/form")
    public String profileWithdraw(Principal principal, Model model) {
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);
        model.addAttribute("now", "profile");
        model.addAttribute("withdrawFormDto", new WithdrawFormDto());
        model.addAttribute("member", member);
        return "members/profile/withdraw";
    }

    // 회원 탈퇴 처리
    @PostMapping(value = "/submit")
    public String profileWithdraw(@Valid WithdrawFormDto withdrawFormDto, BindingResult bindingResult, Model model, Principal principal) {
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);
        if(bindingResult.hasErrors()) {
            System.err.println("Errors: " + bindingResult.getAllErrors());
            model.addAttribute("now", "profile");
            model.addAttribute("member", member);
            return "members/profile/withdraw";
        }
        try {
            // Member 엔티티의 중요 정보 삭제
            member.setPhone(null);
            member.setBirth(null);
            member.setPassword(null);
            member.setRole(Role.WITHDRAW);
            memberRepository.save(member);

            // Withdraw 엔티티에 저장할 정보들을 설정
            Withdraw withdraw = Withdraw.createWithdrawMember(withdrawFormDto);
            withdraw.setEmail(email);

            // Withdraw 엔티티를 저장
            withdrawService.saveWithdrawMember(withdraw);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "회원탈퇴에 실패하였습니다.");
            model.addAttribute("now", "profile");
            model.addAttribute("member", member);
            return "members/profile/withdraw";
        }
        return "redirect:/members/logout";
    }

}

package com.ubn.hairsalon.admin.controller;

import com.ubn.hairsalon.admin.dto.MemberSearchDto;
import com.ubn.hairsalon.member.dto.MemberFormDto;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.repository.MemberRepository;
import com.ubn.hairsalon.member.service.MemberService;
import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ReserveService reserveService;

    @GetMapping(value = {"/list", "/list/{page}"})
    public String customerList(Model model, MemberSearchDto memberSearchDto, @PathVariable("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<Member> members = memberService.getMembersAdminPage(memberSearchDto, pageable);
        model.addAttribute("members", members);
        model.addAttribute("memberSearchDto", memberSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("now", "customer");
        return "admin/customer/list";
    }

    @GetMapping(value = "/info/{id}")
    public String customerInfo(Model model, @PathVariable("id") Long id) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        Member member = memberOptional.isPresent() ? memberOptional.get() : null;
        if(member == null) {
            // member 객체가 존재하지 않는 경우의 처리
            model.addAttribute("errorMessage", "존재하지 않는 회원입니다.");
            return "redirect:/admin/customer/list";
        }
        MemberFormDto memberFormDto = memberService.getProfile(id);
        model.addAttribute("member", member);
        model.addAttribute("now", "customer");
        model.addAttribute("memberFormDto", memberFormDto);
        return "admin/customer/info";
    }

    @PostMapping(value = "/info/{id}")
    @ResponseBody
    public ResponseEntity<?> customerMemoUpdate(@RequestBody Map<String, Object> param, @PathVariable("id") Long id) {
        String memo = (String) param.get("memo");
        Long memberId;
        try {
            // 화면으로부터 넘어온 메모와 회원의 아이디를 이용하여 메모를 수정하는 로직을 호출한 후 반환값으로 int를 받아옴
            memberId = memberService.updateMemberMemo(memo, id);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        // 결과값으로 생성된 멤버Id와 요청이 성공하였다는 HTTP 응답 상태 코드를 반환
        return new ResponseEntity<Long>(memberId, HttpStatus.OK);
    }

    @GetMapping(value = {"/reserved/{id}", "/reserved/{id}/{page}"})
    public String reservedMemberAdmin(@PathVariable("id") Long id, Model model, @PathVariable("page") Optional<Integer> page) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        Member member = memberOptional.isPresent() ? memberOptional.get() : null;
        if(member == null) {
            // member 객체가 존재하지 않는 경우의 처리
            model.addAttribute("errorMessage", "존재하지 않는 회원입니다.");
            return "redirect:/admin/customer/list";
        }
        Pageable pageable = PageRequest.of(page.orElse(0), 5);
        Page<Reserve> reserves = reserveService.getReservedProfilePage(member, pageable);
        model.addAttribute("reserves", reserves);
        model.addAttribute("maxPage", 5);
        model.addAttribute("now", "customer");
        model.addAttribute("memberId", id);
        return "admin/customer/reserved";
    }

}

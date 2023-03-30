package com.ubn.hairsalon.member.controller;

import com.ubn.hairsalon.admin.entity.Type;
import com.ubn.hairsalon.admin.repository.TypeRepository;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.repository.MemberRepository;
import com.ubn.hairsalon.reserve.dto.ReserveFormDto;
import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.reserve.repository.ReserveRepository;
import com.ubn.hairsalon.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/members/reserved")
@RequiredArgsConstructor
public class MemberReservedController {

    private final MemberRepository memberRepository;
    private final ReserveService reserveService;
    private final TypeRepository typeRepository;
    private final ReserveRepository reserveRepository;

    // 회원의 예약정보 목록
    @GetMapping(value = "/list")
    public String reservedMember(Principal principal, Model model, @PathVariable("page") Optional<Integer> page) {
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<Reserve> reserves = reserveService.getReservedProfilePage(member, pageable);
        model.addAttribute("reserves", reserves);
        model.addAttribute("maxPage", 5);
        model.addAttribute("now", "profile");
        return "members/reserved/list";
    }

    @GetMapping(value = "/info/{id}")
    public String reservedDetailPage(@PathVariable("id") Long id, Model model, Principal principal) {
        if (getMembersReserved(id, model, reserveRepository)) return "redirect:/admin/reserved/list";
        return "members/reserved/info";
    }

    public static boolean getMembersReserved(@PathVariable("id") Long id, Model model, ReserveRepository reserveRepository) {
        Optional<Reserve> reserveOptional = reserveRepository.findById(id);
        if (!reserveOptional.isPresent()) {
            // reserve 객체가 존재하지 않는 경우의 처리
            model.addAttribute("errorMessage", "존재하지 않는 예약입니다.");
            return true;
        }
        Reserve reserve = reserveOptional.get();
        model.addAttribute("reserve", reserve);
        return false;
    }

    @GetMapping(value = "/edit/{id}")
    public String reservedDetailForm(@PathVariable("id") Long id, Model model, Principal principal) {
        try {
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            List<Type> types = reserveService.findMembersType(email);
            ReserveFormDto reserveFormDto = reserveService.getReservedDetail(id);
            model.addAttribute("member", member);
            model.addAttribute("types", types);
            model.addAttribute("reserveFormDto", reserveFormDto);
            model.addAttribute("now", "profile");
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "해당 예약이 존재하지 않습니다.");
            model.addAttribute("reserveFormDto", new ReserveFormDto());
            model.addAttribute("now", "profile");
            return "members/reserved/list";
        }
        return "reserve/reserveForm";
    }

    // 예약 수정 시
    @PostMapping(value = "/edit/{id}")
    public String updateProfileReserved(@Valid ReserveFormDto reserveFormDto, Principal principal, BindingResult bindingResult, Model model) {
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);
        List<Type> types = reserveService.findMembersType(email);

        if(bindingResult.hasErrors()) {
            model.addAttribute("now", "profile");
            model.addAttribute("member", member);
            model.addAttribute("types", types);
            return "reserve/reserveForm";
        }
        try {
            Long typeId = reserveFormDto.getTypeId();
            Type type = typeRepository.findById(typeId).orElseThrow(IllegalArgumentException::new);
            reserveService.updateProfileReserved(member, type, reserveFormDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "수정에 실패하였습니다.");
            return "reserve/reserveForm";
        }
        return "redirect:/members/reserved/list";
    }

    // 예약 취소 시
    @GetMapping(value = "/del/{id}")
    public String deleteProfileReserved(@PathVariable("id") Long id, Model model) {
        try {
            reserveService.deleteProfileReserved(id);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "해당 예약이 존재하지 않습니다.");
            model.addAttribute("reserveFormDto", new ReserveFormDto());
            model.addAttribute("now", "profile");
            return "reserve/reserveForm";
        }
        return "redirect:/members/reserved/list";
    }

}

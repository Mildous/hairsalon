package com.ubn.hairsalon.reserve.controller;

import com.ubn.hairsalon.admin.entity.Type;
import com.ubn.hairsalon.admin.repository.TypeRepository;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.repository.MemberRepository;
import com.ubn.hairsalon.reserve.dto.ReserveFormDto;
import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@RequestMapping("/reserve")
@Controller
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;
    private final MemberRepository memberRepository;
    private final TypeRepository typeRepository;

    @GetMapping(value = "/new")
    public String reserveForm(Principal principal, Model model) {
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);
        List<Type> types = reserveService.findMembersType(email);
        model.addAttribute("now", "reserve");
        model.addAttribute("reserveFormDto", new ReserveFormDto());
        model.addAttribute("member", member);
        model.addAttribute("types", types);
        return "reserve/reserveForm";
    }

    @PostMapping(value = "/new")
    public String reserveForm(@Valid ReserveFormDto reserveFormDto, BindingResult bindingResult, Model model, Principal principal) {
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);
        List<Type> types = reserveService.findMembersType(email);
        if(bindingResult.hasErrors()) {
            model.addAttribute("now", "reserve");
            model.addAttribute("member", member);
            model.addAttribute("types", types);
            return "reserve/reserveForm";
        }
        try {
            Long typeId = reserveFormDto.getTypeId();
            Type type = typeRepository.findById(typeId).orElseThrow(IllegalArgumentException::new);
            Reserve reserve = Reserve.createReserve(member, type, reserveFormDto);
            reserveService.saveReserve(reserve);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "reserve/reserveForm";
        }

        return "redirect:/";
    }

    @ResponseBody
    @PostMapping(value = "/getTime")
    public List<String> getTime(@RequestBody Map<String, String> request) throws Exception {
        List<String> times = reserveService.getReservedTime(request.get("rsvDate"));
        return times;
    }

}

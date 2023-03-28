package com.ubn.hairsalon.admin.controller;

import com.ubn.hairsalon.member.service.MemberService;
import com.ubn.hairsalon.withdraw.entity.Withdraw;
import com.ubn.hairsalon.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/admin/withdraw")
@RequiredArgsConstructor
public class WithdrawCustomerController {

    private final WithdrawService withdrawService;

    @GetMapping(value = {"/list", "/list/{page}"})
    public String withdrawList(Model model, @PathVariable("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<Withdraw> withdraws = withdrawService.getWithdrawMembers(pageable);
        model.addAttribute("withdraws", withdraws);
        model.addAttribute("maxPage", 5);
        model.addAttribute("now", "customer");
        return "admin/withdraw/list";
    }

}

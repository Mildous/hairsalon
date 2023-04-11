package com.ubn.hairsalon.admin.controller;

import com.ubn.hairsalon.admin.dto.TypeFormDto;
import com.ubn.hairsalon.admin.entity.Type;
import com.ubn.hairsalon.admin.service.TypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RequestMapping("/admin/type")
@Controller
@RequiredArgsConstructor
public class TypeController {

    private final TypeService typeService;

    @GetMapping(value = "/new")
    public String typeForm(Model model) {
        model.addAttribute("typeFormDto", new TypeFormDto());
        model.addAttribute("now", "type");
        return "admin/type/typeForm";
    }

    @PostMapping(value = "/new")
    public String typeForm(@Valid TypeFormDto typeFormDto, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("now", "type");
            return "admin/type/typeForm";
        }
        try {
            Type type = Type.createType(typeFormDto);
            typeService.saveType(type);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/type/typeForm";
        }

        return "redirect:/admin/type/list";
    }

    @GetMapping(value = "/list")
    public String typeList(Model model) {
        List<Type> types = typeService.listType();
        model.addAttribute("types", types);
        model.addAttribute("now", "type");
        return "admin/type/list";
    }

    @GetMapping(value = "/{typeId}")
    public String typeDetail(@PathVariable("typeId") Long typeId, Model model) {
        try {
            TypeFormDto typeFormDto = typeService.getTypeDetail(typeId);
            model.addAttribute("typeFormDto", typeFormDto);
            model.addAttribute("now", "type");
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "해당 타입이 존재하지 않습니다.");
            model.addAttribute("typeFormDto", new TypeFormDto());
            model.addAttribute("now", "type");
            return "admin/type/list";
        }
        return "admin/type/typeForm";
    }

    @PostMapping(value = "/{typeId}")
    public String typeUpdate(@Valid TypeFormDto typeFormDto, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("now", "type");
            return "admin/type/typeForm";
        }
        try {
            typeService.updateType(typeFormDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "수정 중 오류가 발생하였습니다.");
            return "admin/type/typeForm";
        }

        return "redirect:/admin/type/list";
    }

    @GetMapping(value = "/del/{typeId}")
    public String typeDelete(@PathVariable("typeId") Long typeId, Model model) {
        try {
            typeService.deleteType(typeId);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "해당 타입이 존재하지 않습니다.");
            model.addAttribute("typeFormDto", new TypeFormDto());
            model.addAttribute("now", "type");
            return "admin/type/list";
        }
        return "redirect:/admin/type/list";
    }
}

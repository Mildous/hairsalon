package com.ubn.hairsalon.review.controller;

import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.reserve.repository.ReserveRepository;
import com.ubn.hairsalon.review.dto.ReviewFormDto;
import com.ubn.hairsalon.review.dto.ReviewImgDto;
import com.ubn.hairsalon.review.dto.ReviewSearchDto;
import com.ubn.hairsalon.review.dto.ReviewThumbnailDto;
import com.ubn.hairsalon.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;

import static com.ubn.hairsalon.member.controller.MemberReservedController.getMembersReserved;

@RequestMapping("/review")
@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReserveRepository reserveRepository;

    @GetMapping(value = "/new/{reserveId}")
    public String createReview(Model model, @PathVariable("reserveId") Long reserveId) {
        if (getMembersReserved(reserveId, model, reserveRepository)) return "redirect:/";
        model.addAttribute("reviewFormDto", new ReviewFormDto());
        model.addAttribute("reviewImgDto", new ReviewImgDto());
        model.addAttribute("now", "review");
        return "review/reviewForm";
    }


    /**
     * 스프링에서 비동기 처리 시
     *
     * @RequestBody  : HTTP 요청의 본문 body에 담긴 내용을 자바 객체로 전달
     * @ResponseBody : 자바 객체를 HTTP 요청의 body로 전달
     *
     * But..  file은 JSON으로 보낼 수 없기 때문에 form으로 받아야 함.
     **/
    @PostMapping(value = "/new/{reserveId}")
    @ResponseBody
    public ResponseEntity<?> createReviewAjax(@PathVariable("reserveId") Long id,
                                              @ModelAttribute("reviewFormDto") @Valid ReviewFormDto reviewFormDto,
                                              BindingResult bindingResult,
                                              @RequestParam("reviewImgFile") List<MultipartFile> reviewImgFileList, Model model) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return ResponseEntity.badRequest().body(fieldErrors);
        }

        if (reviewImgFileList.get(0).isEmpty() && reviewFormDto.getReviewId() == null) {
            System.err.println("이미지 첨부 에러");
            return ResponseEntity.badRequest().body("이미지는 1개 이상 등록해야합니다.");
        }

        try {
            Optional<Reserve> reserveOptional = reserveRepository.findById(id);
            if (!reserveOptional.isPresent()) {
                return ResponseEntity.badRequest().body("해당 예약 정보를 찾을 수 없습니다.");
            }
            Reserve reserve = reserveOptional.get();
            reviewService.createReview(reviewFormDto, reviewImgFileList, reserve);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("등록 중 에러");
            return ResponseEntity.badRequest().body("등록 중 오류가 발생하였습니다.");
        }
    }

    @GetMapping(value = {"/list", "/list/{page}"})
    public String reviewList(Model model, ReviewSearchDto reviewSearchDto, @PathVariable("page")Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<ReviewThumbnailDto> reviews = reviewService.getReviewThumbnailPage(reviewSearchDto, pageable);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewSearchDto", reviewSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("now", "review");
        return "review/list";
    }

}
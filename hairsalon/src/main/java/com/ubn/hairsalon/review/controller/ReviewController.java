package com.ubn.hairsalon.review.controller;

import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.reserve.repository.ReserveRepository;
import com.ubn.hairsalon.review.dto.ReviewFormDto;
import com.ubn.hairsalon.review.dto.ReviewImgDto;
import com.ubn.hairsalon.review.dto.ReviewSearchDto;
import com.ubn.hairsalon.review.dto.ReviewThumbnailDto;
import com.ubn.hairsalon.review.entity.Review;
import com.ubn.hairsalon.review.repository.ReviewRepository;
import com.ubn.hairsalon.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityNotFoundException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

import static com.ubn.hairsalon.member.controller.MemberReservedController.getMembersReserved;


/**
 * 스프링에서 비동기 처리 시..
 * @RequestBody  : HTTP 요청의 본문 body에 담긴 내용을 자바 객체로 전달
 * @ResponseBody : 자바 객체를 HTTP 요청의 body로 전달
 *
 * But..  file은 JSON으로 보낼 수 없기 때문에 form으로 받아야 함.
 **/

@RequestMapping("/review")
@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReserveRepository reserveRepository;
    private final ReviewRepository reviewRepository;

    /**
     * Review 등록 버튼 클릭 시 Modal open - Ajax(Modal Window)
     *
     * @param model
     * @param reserveId
     * @return
     */
    @GetMapping(value = "/new/{reserveId}")
    public String createReview(Model model, @PathVariable("reserveId") Long reserveId) {
        if (getMembersReserved(reserveId, model, reserveRepository)) return "redirect:/";
        model.addAttribute("reviewFormDto", new ReviewFormDto());
        model.addAttribute("reviewImgDto", new ReviewImgDto());
        model.addAttribute("now", "review");
        return "review/reviewForm";
    }

    /**
     * Review 등록 시 Post 처리 - Ajax(Modal Window)
     *
     * @param id
     * @param reviewFormDto
     * @param bindingResult
     * @param reviewImgFileList
     * @param model
     * @return
     */
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
            return ResponseEntity.badRequest().body("등록 중 오류가 발생하였습니다.");
        }
    }

    /**
     * Review 목록 처리 - image thumbnail card list
     *
     * @param model
     * @param reviewSearchDto
     * @param page
     * @return
     */
    @GetMapping(value = {"/list", "/list/{page}"})
    public String getReviewList(Model model, ReviewSearchDto reviewSearchDto, @PathVariable("page")Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 8);
        Page<ReviewThumbnailDto> reviews = reviewService.getReviewThumbnailPage(reviewSearchDto, pageable);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewSearchDto", reviewSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("now", "review");
        return "review/list";
    }

    /**
     * Review 상세정보 Get 처리
     *
     * @param model
     * @param reserveId
     * @return
     */
    @GetMapping(value = "/info/{reserveId}")
    public String getReviewDetail(Model model, @PathVariable("reserveId") Long reserveId) {
        if (getMembersReserved(reserveId, model, reserveRepository)) return "redirect:/";
        try {
            ReviewFormDto reviewFormDto = reviewService.getReviewDetail(reserveId);
            model.addAttribute("reviewFormDto", reviewFormDto);
            model.addAttribute("now", "review");
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "해당 리뷰가 존재하지 않습니다.");
            return "redirect:/review/list";
        }
        return "review/reviewForm";
    }

    /**
     * Review 정보 수정 후 update 시 Post 처리
     *
     * @param reviewFormDto
     * @param bindingResult
     * @param reviewImgFileList
     * @return
     */
    @PostMapping(value = "/edit/{reviewId}")
    public ResponseEntity<?> updateReviewAjax(@ModelAttribute("reviewFormDto") @Valid ReviewFormDto reviewFormDto,
                               BindingResult bindingResult, @RequestParam("reviewImgFile") List<MultipartFile> reviewImgFileList) {

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return ResponseEntity.badRequest().body(fieldErrors);
        }

        if (reviewImgFileList.get(0).isEmpty() && reviewFormDto.getReviewImgIds() == null) {
            return ResponseEntity.badRequest().body("이미지는 1개 이상 등록해야합니다.");
        }

        try {
            reviewService.setUpdateReview(reviewFormDto, reviewImgFileList);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("수정 중 오류가 발생하였습니다.");
        }
    }

    /**
     * Review 썸네일에서 more 버튼 클릭 시 상세보기 처리
     * 
     * @param model
     * @param reviewId
     * @return
     */
    @GetMapping(value = "/detail/{reviewId}")
    public String getThumbnailReviewDetail(Model model, @PathVariable("reviewId") Long reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        if (!reviewOptional.isPresent()) {
            // review 객체가 존재하지 않는 경우의 처리
            model.addAttribute("errorMessage", "존재하지 않는 리뷰입니다.");
            return "redirect:/review/list";
        }
        Review review = reviewOptional.get();
        model.addAttribute("review", review);
        model.addAttribute("now", "review");
        return "review/reviewDetail";
    }

    /**
     * Review 정보 삭제 처리
     * 
     * @param reviewId
     * @param principal
     * @return
     */
    @PostMapping(value = "/delete/{reviewId}")
    public @ResponseBody ResponseEntity deleteReview(@PathVariable("reviewId") Long reviewId, Principal principal) {
        if(!reviewService.validateReview(reviewId, principal.getName())) {
            return new ResponseEntity<String>("해당 리뷰의 삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        try {
            reviewService.setDeleteReview(reviewId);
            return new ResponseEntity<String>("리뷰가 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("삭제 도중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
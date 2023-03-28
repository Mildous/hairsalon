package com.ubn.hairsalon.admin.controller;

import com.ubn.hairsalon.admin.dto.ReservedSearchDto;
import com.ubn.hairsalon.member.controller.MemberReservedController;
import com.ubn.hairsalon.reserve.constant.ReserveStatus;
import com.ubn.hairsalon.reserve.constant.ServiceStatus;
import com.ubn.hairsalon.reserve.entity.Reserve;
import com.ubn.hairsalon.reserve.repository.ReserveRepository;
import com.ubn.hairsalon.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin/reserved")
@RequiredArgsConstructor
public class ReservedController {

    private final ReserveService reserveService;
    private final ReserveRepository reserveRepository;

    @GetMapping(value = {"/list", "/list/{page}"})
    public String reservedList(Model model, ReservedSearchDto reservedSearchDto, @PathVariable("page")Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<Reserve> reserves = reserveService.getReservedAdminPage(reservedSearchDto, pageable);
        model.addAttribute("reserves", reserves);
        model.addAttribute("reservedSearchDto", reservedSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("now", "reserved");
        return "admin/reserved/list";
    }

    @GetMapping(value = "/calendar")
    public String reservedCalendar(Model model) {
        model.addAttribute("now", "reserved");
        return "admin/reserved/calendar";
    }

    @ResponseBody
    @GetMapping(value = "/calendar/reservation")
    public List<Map<String, Object>> getCalendarReservation() {
        List<Reserve> reserves = reserveService.getReservedAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for(int i=0; i<reserves.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("title", reserves.get(i).getMember().getName() + " [" + reserves.get(i).getType().getTypeName() + "] ");
            map.put("start", reserves.get(i).getRsvDate() + "T" + reserves.get(i).getRsvStartTime());
            map.put("end", reserves.get(i).getRsvDate() + "T" + reserves.get(i).getRsvEndTime());
            result.add(map);
        }
        return result;
    }

    @GetMapping(value = "/info/{id}")
    public String reservedInfo(Model model, @PathVariable("id") Long id) {
        if (MemberReservedController.getMembersReserved(id, model, reserveRepository))
            return "redirect:/admin/reserved/list";
        model.addAttribute("now", "reserved");
        return "admin/reserved/info";
    }

    @ResponseBody
    @PostMapping(value = "/info/{id}/rStat")
    public ResponseEntity<?> reserveStatus(@RequestParam("reserveStatus") ReserveStatus reserveStatus, @PathVariable("id") Long id) {
        Long reserveId;
        try {
            reserveId = reserveService.reserveStatus(id, reserveStatus);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(reserveId, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/info/{id}/sStat")
    public ResponseEntity<?> serviceStatus(@RequestParam("serviceStatus") ServiceStatus serviceStatus, @PathVariable("id") Long id) {
        Long reserveId;
        try {
            reserveId = reserveService.serviceStatus(id, serviceStatus);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(reserveId, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/info/{id}/{type}")
    public ResponseEntity<?> updateStatus(@RequestParam("status") String status, @PathVariable("type") String type, @PathVariable("id") Long id) {
        Long reserveId;
        try {
            if(type.equals("rStat")) {
                reserveId = reserveService.reserveStatus(id, ReserveStatus.valueOf(status));
            } else if(type.equals("sStat")) {
                reserveId = reserveService.serviceStatus(id, ServiceStatus.valueOf(status));
            } else {
                throw new IllegalArgumentException("잘못된 요청입니다.");
            }
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(reserveId, HttpStatus.OK);
    }

}

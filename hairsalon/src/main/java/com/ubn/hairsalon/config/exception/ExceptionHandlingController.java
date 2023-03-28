package com.ubn.hairsalon.config.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

public class ExceptionHandlingController implements ErrorController {

    private final String ERROR_401_PAGE_PATH = "/error/401";
    private final String ERROR_ETC_PAGE_PATH = "/error/etc";

    @RequestMapping(value = "/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(status.toString()));

        if(status != null) {
            int statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.UNAUTHORIZED.value()) {
                model.addAttribute("msg", "접근 권한이 없습니다.");
                return ERROR_401_PAGE_PATH;
            }
        }

        return ERROR_ETC_PAGE_PATH;
    }
}

package com.Meta_learning.utility;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResource(NoResourceFoundException e, HttpServletRequest request) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception [{}] {}", request.getMethod(), request.getRequestURI(), e);

        if (isApiRequest(request)) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "서비스 처리 중 오류가 발생했습니다.", "message", e.getMessage() != null ? e.getMessage() : ""));
        }

        ModelAndView mav = new ModelAndView("utility/error");
        mav.addObject("errorTitle", "오류가 발생했습니다");
        mav.addObject("errorMessage", "요청을 처리하는 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
        mav.addObject("returnUrl", "/");
        return mav;
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String contentType = request.getHeader("Content-Type");
        String uri = request.getRequestURI();
        return (accept != null && accept.contains("application/json"))
                || (contentType != null && contentType.contains("application/json"))
                || uri.contains("/api/") || uri.contains("/rest/");
    }
}

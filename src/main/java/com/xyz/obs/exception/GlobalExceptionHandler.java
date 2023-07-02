package com.xyz.obs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFound.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RedirectView handleResourceNotFoundException(ResourceNotFound ex) {
        RedirectView redirectView = new RedirectView("/error");
        redirectView.addStaticAttribute("error", ex.getMessage());
        return redirectView;
    }
}

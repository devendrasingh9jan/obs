package com.xyz.obs.controller;

import com.xyz.obs.model.*;
import com.xyz.obs.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("deposit")
public class DepositController {

    @Autowired
    private DepositService depositService;

    @GetMapping("/")
    String login() {
        return "deposit";
    }

    @PostMapping(value = "/create")
    public ResponseEntity<String> createDeposit(@RequestBody DepositRequest depositRequest, HttpSession session, HttpServletResponse response) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            depositRequest.setCustId(user.getId());
            String status = depositService.createDeposit(depositRequest);
            return ResponseEntity.ok(status);
        } else {
            response.sendRedirect("/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping(value = "/close")
    public ResponseEntity<String> close(@RequestParam String depositType, @RequestParam Long depositId, HttpSession session, HttpServletResponse response) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            String status = depositService.close(user.getId(), depositId,depositType);
            return ResponseEntity.ok(status);
        } else {
            response.sendRedirect("/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping(value = "/view")
    public ResponseEntity<List<Deposit>> viewAllDeposits(HttpSession session, HttpServletResponse response) throws IOException {
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            List<Deposit> deposits = depositService.getAllDeposits(user.getId());
            return ResponseEntity.ok(deposits);
        } else {
            response.sendRedirect("/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


}

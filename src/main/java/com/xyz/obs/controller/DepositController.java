package com.xyz.obs.controller;

import com.xyz.obs.model.*;
import com.xyz.obs.repository.DepositView;
import com.xyz.obs.repository.TransactionView;
import com.xyz.obs.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("deposit")
public class DepositController {

    @Autowired
    private DepositService depositService;

    @GetMapping("/")
    public String home(){
        return "deposit";
    }

    @PostMapping(value = "/create/fixed")
    public String createFixedDeposit(@RequestBody FixedDeposit fixedDeposit){
        return depositService.createFixedDeposit(fixedDeposit);
    }
    @PostMapping(value = "/create/recurring")
    public String createFixedDeposit(@RequestBody RecurringDeposit recurringDeposit){
        return depositService.createRecurringDeposit(recurringDeposit);
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

    @PostMapping(value = "/close/fixed")
    public String closeFixedDeposit(@RequestBody FixedDeposit fixedDeposit){
        return depositService.closeFixedDeposit(fixedDeposit);
    }
    @PostMapping(value = "/close/recurring")
    public String closeFixedDeposit(@RequestBody RecurringDeposit recurringDeposit){
        return depositService.closeRecurringDeposit(recurringDeposit);
    }

    @GetMapping(value = "/user/{userId}")
    public List<DepositView> viewAllDeposits(@PathVariable Long userId){
        return depositService.getAllDeposits(userId);
    }

}

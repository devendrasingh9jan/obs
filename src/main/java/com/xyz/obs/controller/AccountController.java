package com.xyz.obs.controller;

import com.xyz.obs.model.Transfer;
import com.xyz.obs.model.User;
import com.xyz.obs.repository.TransactionView;
import com.xyz.obs.service.AccountService;
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
@RequestMapping("account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    public String home(){
        return "account";
    }

    @GetMapping(value = "/summary")
    public ResponseEntity<Double> viewAccountSummary(HttpSession session, HttpServletResponse response) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Double accountSummary = accountService.getAccountSummary(user.getId());
            return ResponseEntity.ok(accountSummary);
        } else {
            response.sendRedirect("/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping(value = "/statement")
    public ResponseEntity<List<TransactionView>> viewAccountStatement(HttpSession session, HttpServletResponse response) throws IOException {
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            List<TransactionView> accountStatement = accountService.getAccountStatement(user.getId());
            return ResponseEntity.ok(accountStatement);
        } else {
            response.sendRedirect("/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * User should know the payee account number or payee id to add.
     * Transfer.userId: loggedIn user or the user who wants to activate payee
     * Transfer.payeeId: userId of the user to which money needs to be transferred.
     * Transfer.amount: amount to be transferred.
     * @return Amount Transferred.
     */
    @PostMapping(value = "/transfer")
    public ResponseEntity<String> transfer(@RequestBody Transfer transfer, HttpSession session, HttpServletResponse response) throws IOException {
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            transfer.setUserId(user.getId());
            String status = accountService.transfer(transfer);
            return ResponseEntity.ok(status);
        } else {
            response.sendRedirect("/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

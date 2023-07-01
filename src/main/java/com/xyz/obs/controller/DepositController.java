package com.xyz.obs.controller;

import com.xyz.obs.model.Deposit;
import com.xyz.obs.model.FixedDeposit;
import com.xyz.obs.model.RecurringDeposit;
import com.xyz.obs.model.Transfer;
import com.xyz.obs.repository.DepositView;
import com.xyz.obs.repository.TransactionView;
import com.xyz.obs.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("deposit")
public class DepositController {

    @Autowired
    private DepositService depositService;
    @PostMapping(value = "/create/fixed")
    public String createFixedDeposit(@RequestBody FixedDeposit fixedDeposit){
        return depositService.createFixedDeposit(fixedDeposit);
    }
    @PostMapping(value = "/create/recurring")
    public String createFixedDeposit(@RequestBody RecurringDeposit recurringDeposit){
        return depositService.createRecurringDeposit(recurringDeposit);
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

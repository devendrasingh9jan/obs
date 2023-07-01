package com.xyz.obs.controller;


import com.xyz.obs.model.Customer;
import com.xyz.obs.model.Transfer;
import com.xyz.obs.model.User;
import com.xyz.obs.repository.UserRepository;
import com.xyz.obs.service.PayeeService;
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

@Controller
@RequestMapping("payee")
public class PayeeController {
    @Autowired
    private PayeeService payeeService;

    @Autowired
    private UserRepository userRepository;

    /*@GetMapping("/")
    public String home(){
        return "payee";
    }*/


    /**
     * User should know the payee account number or payee id to add.
     * userId: loggedIn user or the user who wants to add payee
     * accountId: account number of payee user wants to add
     * @return Payee Added.
     */
    @GetMapping(value = "/accountId")
    public ResponseEntity<String> addPayee(@RequestParam Long accountId, HttpSession session, HttpServletResponse response) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            String status = payeeService.addPayee(user.getId(), accountId);
            return ResponseEntity.ok(status);
        } else {
            response.sendRedirect("/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    /**
     * User should know the payee account number or payee id to add.
     * userId: loggedIn user or the user who wants to activate payee
     * @param payeeId: payeeId of payee user wants to activate
     * @return Payee Activated.
     */
    @GetMapping(value = "/activate")
    public ResponseEntity<String> activatePayee(@RequestParam Long payeeId,HttpSession session, HttpServletResponse response) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            String status = payeeService.activatePayee(user.getId(), payeeId);
            return ResponseEntity.ok(status);
        } else {
            response.sendRedirect("/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping(value = "/")
    public ModelAndView viewPayee(HttpSession session, HttpServletResponse response) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<Customer> payees = payeeService.getPayee(user.getId());
            return new ModelAndView("payee", "payeeList", payees);
        } else {
            response.sendRedirect("/login");
        }
        return new ModelAndView("payee", "payeeList", Collections.EMPTY_LIST);
    }

    /**
     * User should know the payee account number or payee id to add.
     * userId: loggedIn user or the user who wants to activate payee
     * payeeId: account number of payee user wants to activate
     * @return Payee Deleted.
     */
    /*@DeleteMapping(value = "/userId/{userId}/accountId/{accountId}")
    public String deletePayee(@PathVariable Long userId, @PathVariable Long accountId){
        return payeeService.deletePayee(userId,accountId);
    }*/

    @GetMapping(value = "/delete")
    public ResponseEntity<String> deletePayee(@RequestParam Long payeeId,HttpSession session, HttpServletResponse response) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            String status = payeeService.deletePayee(user.getId(), payeeId);
            return ResponseEntity.ok(status);
        } else {
            response.sendRedirect("/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


}
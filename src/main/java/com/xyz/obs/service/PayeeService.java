package com.xyz.obs.service;

import com.xyz.obs.exception.ResourceNotFound;
import com.xyz.obs.model.Account;
import com.xyz.obs.model.Customer;
import com.xyz.obs.model.Payee;
import com.xyz.obs.model.Transfer;
import com.xyz.obs.repository.*;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PayeeService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final PayeeRepository payeeRepository;

    @Autowired
    public PayeeService(UserRepository userRepository,
                       CustomerRepository customerRepository,
                       AccountRepository accountRepository,
                       TransactionRepository transactionRepository,
                       PayeeRepository payeeRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.payeeRepository = payeeRepository;
    }

    public String addPayee(Long userId, Long accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Optional<Payee> addedPayeeOptional = payeeRepository.findByCustomerIdAndCustomerPayeeId(userId, account.getCustomer().getId());
            if (addedPayeeOptional.isPresent()) {
                throw new ResourceNotFound("Payee is already added with the  AccountId:"+ accountId);
            }
            Payee payee = new Payee();
            payee.setCustomerId(userId);
            payee.setCustomerPayeeId(account.getCustomer().getId());
            payee.setIsActive(false);
            payeeRepository.save(payee);
            return "Payee Added";
        } else{
            throw new ResourceNotFound("Account not found with AccountId:"+ accountId);
        }
    }

    public String activatePayee(Long userId, Long customerPayeeId) {
            Optional<Payee> payeeOptional = payeeRepository.findByCustomerIdAndCustomerPayeeId(userId, customerPayeeId);
        if (payeeOptional.isPresent()) {
            Payee payee = payeeOptional.get();
            if(BooleanUtils.isTrue(payee.getIsActive())){
                return "Payee Already Activated";
            }
            payee.setIsActive(true);
            payeeRepository.save(payee);
            return "Payee Activated";
        }else{
            throw new ResourceNotFound("Please add Payee First.");
        }
    }

    public List<Customer> getPayee(Long userId) {
        List<Payee> payeeList = payeeRepository.findByCustomerId(userId);
        List<Customer> payees = new ArrayList<>();
        for (Payee payee : payeeList) {
            Optional<Customer> customerOptional = customerRepository.findById(payee.getCustomerPayeeId());
            customerOptional.ifPresent(payees::add);
        }
        return payees;
    }

    public String deletePayee(Long userId,Long customerPayeeId) {
        Optional<Payee> payeeOptional = payeeRepository.findByCustomerIdAndCustomerPayeeId(userId, customerPayeeId);
        if (payeeOptional.isPresent()) {
            Payee payee = payeeOptional.get();
            payeeRepository.delete(payee);
            return "Payee Deleted";
        } else {
            throw new ResourceNotFound("Add Payee to delete.");
        }
    }

}

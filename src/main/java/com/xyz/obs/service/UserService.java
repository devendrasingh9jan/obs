package com.xyz.obs.service;

import com.xyz.obs.exception.ResourceNotFound;
import com.xyz.obs.model.*;
import com.xyz.obs.repository.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       CustomerRepository customerRepository,
                       AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }


    public void saveUser(Customer customer) {
        if (isUserExists(customer.getUser().getUsername())) {
            throw new ResourceNotFound("Username already exists");
        }
        Branch branch = new Branch();
        branch.setBankName("XYZ");
        branch.setBranchName("123 Main St");
        branch.setAddress("123 Main St Agra");
        customer.setBranch(branch);
        customerRepository.save(customer);
        Account account = new Account();
        account.setAccountType("savings");
        account.setCustomer(customer);
        account.setBalance(50000.00);
        accountRepository.save(account);
    }

    public boolean isUserExists(String username) {
        User existingUser = userRepository.findByUsername(username);
        return existingUser != null;
    }

    public void updateUser(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        boolean matches = user.getOldPassword().equals(existingUser.getPassword());
        if (matches){
            existingUser.setPassword(user.getPassword());
            userRepository.save(existingUser);
        } else {
            throw new IllegalArgumentException("User password did not matched.");
        }

    }
}
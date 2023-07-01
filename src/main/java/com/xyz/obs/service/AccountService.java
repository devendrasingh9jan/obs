package com.xyz.obs.service;

import com.xyz.obs.exception.ResourceNotFound;
import com.xyz.obs.model.*;
import com.xyz.obs.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PayeeRepository payeeRepository;

    @Autowired
    public AccountService(CustomerRepository customerRepository,
                       AccountRepository accountRepository,
                       TransactionRepository transactionRepository,
                       PayeeRepository payeeRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.payeeRepository = payeeRepository;
    }

    private static final DecimalFormat decfor = new DecimalFormat("0.00");

    public Double getAccountSummary(Long userId) {
        Long accountId = getCustomerId(userId);
        Double currentBalance = 0.00;
        Optional<Account> accountOptional = accountRepository.findByCustomerId(accountId);
        if (accountOptional.isPresent()) {
            currentBalance = accountOptional.get().getBalance();
        }
        String formatted = decfor.format(currentBalance);
        return Double.valueOf(formatted);
    }

    public List<TransactionView> getAccountStatement(Long userId) {
        Long customerId = getCustomerId(userId);
        Optional<Account> accountOptional = accountRepository.findByCustomerId(customerId);
        List<TransactionView> transactions = null;
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Long accountId = account.getId();
            transactions = transactionRepository.findAllByAccountId(accountId);
        }
        return transactions;
    }



    private Long getCustomerId(Long userId) {
        Optional<Customer> customerOptional = customerRepository.findByUserId(userId);
        Long accountId = null;
        if (customerOptional.isPresent()) {
            accountId = customerOptional.get().getId();
        }
        return accountId;
    }

    public String transfer(Transfer transfer) {
        Optional<Payee> payeeOptional = payeeRepository.findByCustomerIdAndCustomerPayeeId(transfer.getUserId(), transfer.getPayeeId());
        if (payeeOptional.isPresent()) {
            Payee payee = payeeOptional.get();
            Optional<Customer> customerOptional = customerRepository.findByUserId(payee.getCustomerId());
            Optional<Customer> customerPayeeOptional = customerRepository.findByUserId(payee.getCustomerPayeeId());
            Branch customerBranch = null;
            Branch customerPayeeBranch = null;
            if (customerOptional.isPresent() && customerPayeeOptional.isPresent()) {
                customerBranch = customerOptional.get().getBranch();
                customerPayeeBranch = customerPayeeOptional.get().getBranch();
            }
            Optional<Account> customerAccountOptional = accountRepository.findByCustomerId(payee.getCustomerId());
            Optional<Account> payeeAccountOptional = accountRepository.findByCustomerId(payee.getCustomerPayeeId());
            if(customerAccountOptional.isPresent() && payeeAccountOptional.isPresent()
                    && Objects.requireNonNull(customerBranch).getBankName().equals("XYZ")
                    && Objects.requireNonNull(customerPayeeBranch).getBankName().equals("XYZ") ){
                Account customerAccount = customerAccountOptional.get();
                Account payeeAccount = payeeAccountOptional.get();
                if(customerAccount.getBalance()>0 && transfer.getAmount()>0 && customerAccount.getBalance()>= transfer.getAmount()){
                    Double updatedCustomerBalance = customerAccount.getBalance() - transfer.getAmount();
                    customerAccount.setBalance(updatedCustomerBalance);
                    Double updatedPayeeBalance = payeeAccount.getBalance() + transfer.getAmount();
                    payeeAccount.setBalance(updatedPayeeBalance);
                    Transaction transactionCustomer = new Transaction();
                    transactionCustomer.setDate(new Date());
                    transactionCustomer.setTransactionType("Debit");
                    transactionCustomer.setAmount(transfer.getAmount());
                    transactionCustomer.setAccount(customerAccount);
                    transactionRepository.save(transactionCustomer);
                    Transaction transactionPayeeCustomer = new Transaction();
                    transactionPayeeCustomer.setDate(new Date());
                    transactionPayeeCustomer.setTransactionType("Credit");
                    transactionPayeeCustomer.setAmount(transfer.getAmount());
                    transactionPayeeCustomer.setAccount(payeeAccount);
                    transactionRepository.save(transactionPayeeCustomer);
                    accountRepository.save(customerAccount);
                    accountRepository.save(payeeAccount);

                    return "Money Transferred";
                } else {
                    throw new ResourceNotFound(" Insufficient balance.");
                }

            } else {
                throw new ResourceNotFound(" No payee found.");
            }
        }
        throw new ResourceNotFound(" Money Transfer failed");
        //return "Money Transfer failed";
    }
}

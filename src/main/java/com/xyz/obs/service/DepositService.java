package com.xyz.obs.service;

import com.xyz.obs.exception.ResourceNotFound;
import com.xyz.obs.model.*;
import com.xyz.obs.repository.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepositService {

    public static final String FIXED = "fixed";
    public static final String RECURRING = "recurring";
    private final DepositRepository depositRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    @Autowired
    public DepositService(DepositRepository depositRepository, CustomerRepository customerRepository, AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {
        this.depositRepository = depositRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public String createDeposit(DepositRequest depositRequest) {
        if (StringUtils.equals(depositRequest.getDepositType(), FIXED)){
            FixedDeposit fixedDeposit = new FixedDeposit();
            fixedDeposit.setCustId(depositRequest.getCustId());
            fixedDeposit.setDepositType(depositRequest.getDepositType());
            fixedDeposit.setPrincipal(depositRequest.getPrincipal());
            fixedDeposit.setRate(depositRequest.getRate());
            fixedDeposit.setYears(depositRequest.getYears());
            return createFixedDeposit(fixedDeposit);
        } else if (StringUtils.equals(depositRequest.getDepositType(), RECURRING)){
            RecurringDeposit recurringDeposit = new RecurringDeposit();
            recurringDeposit.setCustId(depositRequest.getCustId());
            recurringDeposit.setDepositType(depositRequest.getDepositType());
            recurringDeposit.setPrincipal(depositRequest.getPrincipal());
            recurringDeposit.setRate(depositRequest.getRate());
            recurringDeposit.setMonths(depositRequest.getMonths());
            return createRecurringDeposit(recurringDeposit);
        } else{
            throw new ResourceNotFound("Wrong Deposit Type");
        }
    }

    public String createFixedDeposit(FixedDeposit fixedDeposit) {
        Optional<Customer> customerOptional = customerRepository.findByUserId(fixedDeposit.getCustId());
        if(customerOptional.isPresent()){
            Customer customer = customerOptional.get();
            fixedDeposit.setCustomer(customer);
            Optional<Account> accountOptional = accountRepository.findByCustomerId(fixedDeposit.getCustId());
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                if (account.getBalance()>0 && fixedDeposit.getPrincipal()>0 && account.getBalance()>= fixedDeposit.getPrincipal()) {
                    Double updatedBalance = account.getBalance() - fixedDeposit.getPrincipal();
                    fixedDeposit.setIsActive(true);
                    account.setBalance(updatedBalance);
                    Transaction transactionCustomer = new Transaction();
                    transactionCustomer.setDate(new Date());
                    transactionCustomer.setTransactionType("Debit");
                    transactionCustomer.setAmount(Double.valueOf(fixedDeposit.getPrincipal()));
                    transactionCustomer.setAccount(account);
                    transactionRepository.save(transactionCustomer);
                    accountRepository.save(account);
                    FixedDeposit deposit = depositRepository.save(fixedDeposit);
                    return "Fixed deposit created with Id:"+deposit.getId();
                } else {
                    throw new ResourceNotFound("Insufficient balance.");
                }
            } else {
                throw new ResourceNotFound("Account Not found to create fixed deposit.");
            }
        } else{
            throw new ResourceNotFound("Customer Not found to create fixed deposit.");
        }
    }

    public String createRecurringDeposit(RecurringDeposit recurringDeposit) {
        Optional<Customer> customerOptional = customerRepository.findByUserId(recurringDeposit.getCustId());
        if(customerOptional.isPresent()){
            Customer customer = customerOptional.get();
            recurringDeposit.setCustomer(customer);
            Optional<Account> accountOptional = accountRepository.findByCustomerId(recurringDeposit.getCustId());
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                if (account.getBalance()>0 && recurringDeposit.getPrincipal()>100 && recurringDeposit.getMonths()>6 && account.getBalance()>= recurringDeposit.getPrincipal()) {
                    Double updatedBalance = account.getBalance() - recurringDeposit.getPrincipal();
                    recurringDeposit.setIsActive(true);
                    account.setBalance(updatedBalance);
                    Transaction transactionCustomer = new Transaction();
                    transactionCustomer.setDate(new Date());
                    transactionCustomer.setTransactionType("Debit");
                    transactionCustomer.setAmount(Double.valueOf(recurringDeposit.getPrincipal()));
                    transactionCustomer.setAccount(account);
                    transactionRepository.save(transactionCustomer);
                    accountRepository.save(account);
                    RecurringDeposit deposit = depositRepository.save(recurringDeposit);
                    return "Recurring deposit created with Id:"+deposit.getId();
                } else {
                    throw new ResourceNotFound("Insufficient balance or minimum tenure should be 6 months.");
                }
            } else {
                throw new ResourceNotFound("Account Not found to create fixed deposit.");
            }
        } else{
            throw new ResourceNotFound("Customer Not found to create fixed deposit.");
        }
    }

    public List<DepositView> getAllDeposits(Long userId) {
        return depositRepository.findAllByCustomerId(userId);
    }


    /**
     *
     * For compound interest FD, the FD return calculator uses the following formula –
     *
     * M= P + P {(1 + r/100) t – 1}, where –
     *
     * P is the principal amount
     * r is the rate of interest per period
     * t is the tenure
     */
    public String closeFixedDeposit(FixedDeposit fixedDepositRequest) {
        List<Deposit> deposits = depositRepository.findByCustomerId(fixedDepositRequest.getCustId()).stream().filter(e -> e.getDepositType().equals("fixed")).collect(Collectors.toList());
        Optional<Account> accountOptional = accountRepository.findByCustomerId(fixedDepositRequest.getCustId());
        for (Deposit deposit : deposits) {
            if(Objects.nonNull(fixedDepositRequest.getDepositId()) && deposit.getId().equals(fixedDepositRequest.getDepositId())
                && deposit instanceof FixedDeposit && accountOptional.isPresent()  && BooleanUtils.isTrue(deposit.getIsActive())){
                FixedDeposit fixedDeposit = (FixedDeposit) deposit;
                Integer compoundingFrequency = 1; // Compounded annually
                Double maturityAmount = fixedDeposit.getPrincipal() * Math.pow(1 + (fixedDeposit.getRate() / compoundingFrequency), (compoundingFrequency * fixedDeposit.getYears()));;
                Account account = accountOptional.get();
                Double updatedBalance = account.getBalance() + maturityAmount;
                fixedDeposit.setIsActive(false);
                account.setBalance(updatedBalance);
                Transaction transactionCustomer = new Transaction();
                transactionCustomer.setDate(new Date());
                transactionCustomer.setTransactionType("Credit");
                transactionCustomer.setAmount(maturityAmount);
                transactionCustomer.setAccount(account);
                transactionRepository.save(transactionCustomer);
                accountRepository.save(account);
                depositRepository.save(fixedDeposit);
                return "closed";
            } else{
                throw new ResourceNotFound("Getting error in closing Fixed Deposit.");
            }
        }
        return "not closed";
    }

    public String closeRecurringDeposit(RecurringDeposit recurringDepositRequest) {
        List<Deposit> deposits = depositRepository.findByCustomerId(recurringDepositRequest.getCustId()).stream().filter(e -> e.getDepositType().equals("recurring")).collect(Collectors.toList());
        Optional<Account> accountOptional = accountRepository.findByCustomerId(recurringDepositRequest.getCustId());
        for (Deposit deposit : deposits) {
            if(Objects.nonNull(recurringDepositRequest.getDepositId()) && deposit.getId().equals(recurringDepositRequest.getDepositId())
                    && deposit instanceof RecurringDeposit && accountOptional.isPresent() && BooleanUtils.isTrue(deposit.getIsActive())){
                RecurringDeposit recurringDeposit = (RecurringDeposit) deposit;
                Integer compoundingFrequency = 1; // Compounded annually
                Double maturityAmount = calculateMaturityAmount(recurringDeposit.getPrincipal(), recurringDeposit.getRate(), compoundingFrequency, recurringDeposit.getMonths());
                Account account = accountOptional.get();
                Double updatedBalance = account.getBalance() + maturityAmount;
                recurringDeposit.setIsActive(false);
                account.setBalance(updatedBalance);
                Transaction transactionCustomer = new Transaction();
                transactionCustomer.setDate(new Date());
                transactionCustomer.setTransactionType("Credit");
                transactionCustomer.setAmount(maturityAmount);
                transactionCustomer.setAccount(account);
                transactionRepository.save(transactionCustomer);
                accountRepository.save(account);
                depositRepository.save(recurringDeposit);
                return "closed";
            } else{
                throw new ResourceNotFound("Getting error in closing Recurring Deposit.");
            }
        }
        return "not closed";
    }

    private static double calculateMaturityAmount(double monthlyDeposit, double interestRate, Integer compoundingFrequency, int tenureMonths) {
        double rate = interestRate / 100; // Convert interest rate to decimal form
        double maturityAmount = 0;

        for (int i = 0; i < tenureMonths; i++) {
            maturityAmount += monthlyDeposit;
            maturityAmount += maturityAmount * (rate / compoundingFrequency);
        }

        return maturityAmount;
    }
}

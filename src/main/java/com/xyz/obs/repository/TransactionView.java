package com.xyz.obs.repository;

import java.util.Date;

public interface TransactionView{

        Long getId();
        String getTransactionType();
        Double getAmount();
        Date getDate();
    }
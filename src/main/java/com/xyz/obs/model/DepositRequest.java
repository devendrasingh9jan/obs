package com.xyz.obs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Transient;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequest {
    private String depositType;

    //private Double rate;

    private Long principal;

    //using in case of Fixed Deposit
    private Integer years;

    //using in case of Recurring Deposit
    private Integer months;

    private Long custId;

    private Long depositId;

}

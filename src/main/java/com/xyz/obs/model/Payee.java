package com.xyz.obs.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payees")
public class Payee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*@ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;*/
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_payee_id")
    private Long customerPayeeId;

    private Boolean isActive;
}
package com.xyz.obs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deposits")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "deposit_type")
public class Deposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deposit_type", insertable = false, updatable = false)
    private String depositType;

    private Double rate;

    private Long principal;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Boolean isActive;

    @Transient
    private Long custId;

    @Transient
    private Long depositId;

}

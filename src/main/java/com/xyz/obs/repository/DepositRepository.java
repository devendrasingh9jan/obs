package com.xyz.obs.repository;

import com.xyz.obs.model.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit,Long> {
    List<DepositView> findAllByCustomerId(Long customerId);
    List<Deposit> findByCustomerId(Long customerId);
}

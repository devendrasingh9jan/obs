package com.xyz.obs.repository;

import com.xyz.obs.model.Payee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayeeRepository extends JpaRepository<Payee, Long> {
    Optional<Payee> findByCustomerIdAndCustomerPayeeId(Long userId, Long customerPayeeId);

    List<Payee> findByCustomerId(Long userId);
}

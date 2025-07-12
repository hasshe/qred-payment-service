package com.qred.qredpaymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.qred.qredpaymentservice.repository.entities.Contract;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {
    Optional<Contract> findByContractNumber(String contractNumber);
}

package com.qred.qredpaymentservice.repository;

import com.qred.qredpaymentservice.repository.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByContract_ContractNumber(String contractContractNumber);

    List<Payment> findAllByClient_Id(String clientId);
}

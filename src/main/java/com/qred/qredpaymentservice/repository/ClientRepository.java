package com.qred.qredpaymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.qred.qredpaymentservice.repository.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
}

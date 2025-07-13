package com.qred.qredpaymentservice.service;

import com.qred.qredpaymentservice.repository.ClientRepository;
import com.qred.qredpaymentservice.repository.ContractRepository;
import com.qred.qredpaymentservice.repository.PaymentRepository;
import com.qred.qredpaymentservice.repository.entities.Client;
import com.qred.qredpaymentservice.repository.entities.Contract;
import com.qred.qredpaymentservice.repository.entities.Payment;
import com.qred.qredpaymentservice.service.domain.ContractStatus;
import com.qred.qredpaymentservice.service.domain.DomainPayment;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentsService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentsService.class.getName());

    private final ClientRepository clientRepository;
    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;
    private final PaymentsFileService paymentsFileService;

    @Autowired
    public PaymentsService(ClientRepository clientRepository, PaymentRepository paymentRepository, ContractRepository contractRepository, PaymentsFileService paymentsFileService) {
        this.clientRepository = clientRepository;
        this.paymentRepository = paymentRepository;
        this.contractRepository = contractRepository;
        this.paymentsFileService = paymentsFileService;
    }

    public List<DomainPayment> getPaymentsByContractNumber(String contractNumber) {
        verifyContract(contractNumber);
        List<Payment> payments = paymentRepository.findAllByContract_ContractNumber(contractNumber);
        return payments.stream().map(DomainPayment::toDomain).toList();
    }

    public List<DomainPayment> getPaymentsByClientId(String clientId) {
        verifyClient(clientId);
        List<Payment> payments = paymentRepository.findAllByClient_Id(clientId);
        return payments.stream().map(DomainPayment::toDomain).toList();
    }

    @Transactional
    public DomainPayment createPayment(DomainPayment domainPayment) {
        return SaveAndReturnPayment(domainPayment);
    }

    @Transactional
    public List<DomainPayment> uploadFile(MultipartFile file, String clientId) {
        try {
            var domainPayments = paymentsFileService.processPaymentsFile(file, clientId);
            return domainPayments.stream().map(this::SaveAndReturnPayment).toList();
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Something went wrong while processing file. Message: %s", e.getMessage()));
        }
    }

    private DomainPayment SaveAndReturnPayment(DomainPayment domainPayment) {
        var newPayment = new Payment();
        var existingClient = verifyClient(domainPayment.clientId());
        var existingContract = verifyContract(domainPayment.contractNumber());
        newPayment.setAmount(domainPayment.amount());
        newPayment.setClient(existingClient);
        newPayment.setContract(existingContract);
        newPayment.setType(domainPayment.paymentType().name());
        newPayment.setPaymentDate(domainPayment.paymentDate());
        var savedPayment = paymentRepository.saveAndFlush(newPayment);
        return DomainPayment.toDomain(savedPayment);
    }

    private Client verifyClient(String clientId) {
        var client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            throw new IllegalArgumentException("Client not found");
        }
        return client.get();
    }

    private Contract verifyContract(String contractNumber) {
        var contract = contractRepository.findByContractNumber(contractNumber);
        if (contract.isEmpty()) {
            throw new IllegalArgumentException("Contract not found");
        }
        if (ContractStatus.fromString(contract.get().getStatus()) != ContractStatus.ACTIVE) {
            throw new IllegalArgumentException("Contract status not active");
        }
        var today = LocalDate.now();
        if (today.isBefore(contract.get().getStartDate()) || today.isAfter(contract.get().getEndDate())) {
            throw new IllegalArgumentException("Contract hasn't started yet or has expired");
        }
        return contract.get();
    }
}

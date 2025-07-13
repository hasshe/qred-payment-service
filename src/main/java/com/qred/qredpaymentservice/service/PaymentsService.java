package com.qred.qredpaymentservice.service;

import com.qred.qredpaymentservice.repository.ClientRepository;
import com.qred.qredpaymentservice.repository.ContractRepository;
import com.qred.qredpaymentservice.repository.PaymentRepository;
import com.qred.qredpaymentservice.repository.entities.Client;
import com.qred.qredpaymentservice.repository.entities.Contract;
import com.qred.qredpaymentservice.repository.entities.Payment;
import com.qred.qredpaymentservice.service.domain.DomainPayment;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
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
            throw new RuntimeException(String.format("Something went wrong while processing file. Message: %s", e.getMessage()));
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
        return DomainPayment.toDomain(paymentRepository.save(newPayment));
    }

    private Client verifyClient(String clientId) {
        var client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            throw new RuntimeException("Client not found");
        }
        return client.get();
    }

    private Contract verifyContract(String contractNumber) {
        var contract = contractRepository.findById(contractNumber);
        if (contract.isEmpty()) {
            throw new RuntimeException("Contract not found");
        }
        return contract.get();
    }
}

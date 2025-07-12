package com.qred.qredpaymentservice.service;

import com.qred.qredpaymentservice.service.domain.DomainPayment;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.qred.qredpaymentservice.repository.ClientRepository;
import com.qred.qredpaymentservice.repository.ContractRepository;
import com.qred.qredpaymentservice.repository.PaymentRepository;
import com.qred.qredpaymentservice.repository.entities.Payment;

import java.util.List;

@Service
public class PaymentsService {

    private static Logger logger = LoggerFactory.getLogger(PaymentsService.class.getName());

    private final ClientRepository clientRepository;
    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;

    @Autowired
    public PaymentsService(ClientRepository clientRepository, PaymentRepository paymentRepository, ContractRepository contractRepository) {
        this.clientRepository = clientRepository;
        this.paymentRepository = paymentRepository;
        this.contractRepository = contractRepository;
    }

    public List<DomainPayment> getPaymentsByContractNumber(String contractNumber) {
        //TODO: Validate contract number exists before attempting to fetch
        List<Payment> payments = paymentRepository.findAllByContract_ContractNumber(contractNumber);
        return payments.stream().map(DomainPayment::toDomain).toList();
    }

    public List<DomainPayment> getPaymentsByClientId(String clientId) {
        //TODO: Validate client id exists before attempting to fetch
        List<Payment> payments = paymentRepository.findAllByClient_Id(clientId);
        return payments.stream().map(DomainPayment::toDomain).toList();
    }

    @Transactional
    public DomainPayment createPayment(DomainPayment domainPayment) {
        // TODO: validate before fetching
        var newPayment = new Payment();
        var existingClient = clientRepository.findById(domainPayment.clientId());
        var existingContract = contractRepository.findByContractNumber(domainPayment.contractNumber());
        newPayment.setAmount(domainPayment.amount());
        newPayment.setClient(existingClient.get());
        newPayment.setContract(existingContract.get());
        newPayment.setType(domainPayment.paymentType().name());
        return DomainPayment.toDomain(paymentRepository.save(newPayment));
    }

    @Transactional
    public List<String> uploadFile(MultipartFile file) {
        return null;
    }
}

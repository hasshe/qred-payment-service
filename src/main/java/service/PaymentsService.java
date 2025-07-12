package service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import repository.ClientRepository;
import repository.ContractRepository;
import repository.PaymentRepository;

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

    public List<String> getPaymentsByContractNumber(String contractNumber) {
        return List.of();
    }

    public List<String> getPaymentsByClientId(String customerNumber) {
        return List.of();
    }

    @Transactional
    public String createPayment(String value) {
        return null;
    }

    @Transactional
    public List<String> uploadFile(MultipartFile file) {
        return null;
    }
}

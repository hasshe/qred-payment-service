package service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PaymentsService {

    private static Logger logger = LoggerFactory.getLogger(PaymentsService.class.getName());

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

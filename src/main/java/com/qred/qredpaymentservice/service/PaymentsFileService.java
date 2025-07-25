package com.qred.qredpaymentservice.service;

import com.qred.qredpaymentservice.service.domain.DomainPayment;
import com.qred.qredpaymentservice.utils.csv_processor.CsvFileParser;
import com.qred.qredpaymentservice.utils.xml_processor.XmlFileParser;
import jakarta.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class PaymentsFileService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentsFileService.class.getName());

    public List<DomainPayment> processPaymentsFile(MultipartFile file, String clientId) throws IOException, JAXBException {
        logger.info("Process Payment Request");
        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            throw new IllegalArgumentException("File name is null");
        }

        String fileExtension = fileName.substring(fileName.lastIndexOf('.'));

        return switch (fileExtension.toLowerCase()) {
            case ".csv" -> CsvFileParser.processCsvFile(file, clientId);
            case ".xml" -> XmlFileParser.processXmlFile(file, clientId);
            default -> throw new IllegalArgumentException("File extension is invalid");
        };
    }
}

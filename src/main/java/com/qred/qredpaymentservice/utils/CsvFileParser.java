package com.qred.qredpaymentservice.utils;

import com.qred.qredpaymentservice.service.domain.DomainPayment;
import com.qred.qredpaymentservice.service.domain.PaymentType;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvFileParser {

    public static final int EXPECTED_CSV_LINE_LENGTH = 4;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static List<DomainPayment> processCsvFile(MultipartFile file, String clientId) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            List<DomainPayment> payments = new ArrayList<>();
            String csvLine;
            boolean isFirstLine = true;

            while ((csvLine = bufferedReader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] values = parseCsvLine(csvLine);
                if (values.length == EXPECTED_CSV_LINE_LENGTH) {
                    DomainPayment payment = createPaymentFromCsvValues(values, clientId);
                    payments.add(payment);
                }
            }
            return payments;
        }
    }

    private static String[] parseCsvLine(String csvLine) {
        return csvLine.split(",\\s*");
    }

    private static DomainPayment createPaymentFromCsvValues(String[] values, String clientId) {
        LocalDate paymentDate = LocalDate.parse(values[0].trim(), DATE_FORMATTER);
        BigDecimal amount = new BigDecimal(values[1].trim());
        String type = values[2].trim();
        String contractNumber = values[3].trim();
        return new DomainPayment(paymentDate, amount, PaymentType.fromString(type), contractNumber, clientId);
    }
}

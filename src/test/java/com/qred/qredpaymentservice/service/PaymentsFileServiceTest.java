package com.qred.qredpaymentservice.service;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PaymentsFileServiceTest {

    @InjectMocks
    private PaymentsFileService paymentsFileService;

    @Test
    void processPaymentsFileXml() throws JAXBException, IOException {
        String xmlContent = """
            <payments>
                <payment>
                    <payment_date>2024-01-30</payment_date>
                    <amount>1000.00</amount>
                    <type>incoming</type>
                    <contract_number>12345</contract_number>
                </payment>
            </payments>
            """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.xml",
                "application/xml",
                xmlContent.getBytes()
        );
        var result = paymentsFileService.processPaymentsFile(file, "1");
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().paymentDate()).isEqualTo(LocalDate.parse("2024-01-30"));
        assertThat(result.getFirst().amount()).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    void processPaymentsFileCsv() throws JAXBException, IOException {
        String csvContent = "payment_date,amount,type,contract_number\n2024-01-30,1000.00,incoming,123";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.csv",
                "text/csv",
                csvContent.getBytes()
        );
        var result = paymentsFileService.processPaymentsFile(file, "1");
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().paymentDate()).isEqualTo(LocalDate.parse("2024-01-30"));
        assertThat(result.getFirst().amount()).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    void shouldThrowIfInvalidTypeOfFile() {
        String content = "some random content";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.txt",
                "text/plain",
                content.getBytes()
        );
        assertThrows(IllegalArgumentException.class, () -> paymentsFileService.processPaymentsFile(file, "1"));
    }
}
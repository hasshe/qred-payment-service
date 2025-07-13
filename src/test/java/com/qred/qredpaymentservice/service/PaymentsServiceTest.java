package com.qred.qredpaymentservice.service;

import com.qred.qredpaymentservice.repository.ClientRepository;
import com.qred.qredpaymentservice.repository.ContractRepository;
import com.qred.qredpaymentservice.repository.PaymentRepository;
import com.qred.qredpaymentservice.repository.entities.Client;
import com.qred.qredpaymentservice.repository.entities.Contract;
import com.qred.qredpaymentservice.repository.entities.Payment;
import com.qred.qredpaymentservice.service.domain.ContractStatus;
import com.qred.qredpaymentservice.service.domain.DomainPayment;
import com.qred.qredpaymentservice.service.domain.PaymentType;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PaymentsServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private PaymentsFileService paymentsFileService;

    @InjectMocks
    private PaymentsService paymentsService;

    private Client testClient;
    private Contract testContract;
    private DomainPayment testDomainPayment;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId("1");
        testClient.setName("Client1");
        testClient.setEmail("email@email.com");
        testClient.setAddress("address1");

        testContract = new Contract();
        testContract.setContractNumber("123");
        testContract.setStatus("ACTIVE");
        testContract.setClient(testClient);
        testContract.setStartDate(LocalDate.now().minusYears(1));
        testContract.setEndDate(LocalDate.now().plusYears(1));

        testDomainPayment = new DomainPayment(
                LocalDate.parse("2025-01-01"),
                new BigDecimal(1000),
                PaymentType.INCOMING,
                "123",
                "1"
        );

        testPayment = new Payment();
        testPayment.setPaymentDate(LocalDate.parse("2025-01-01"));
        testPayment.setAmount(new BigDecimal(1000));
        testPayment.setPaymentDate(LocalDate.parse("2025-01-01"));
        testPayment.setClient(testClient);
        testPayment.setContract(testContract);
        testPayment.setType("INCOMING");
    }

    @Test
    void shouldGetPaymentsByContractNumber() {
        when(contractRepository.findByContractNumber("123")).thenReturn(Optional.of(testContract));
        when(paymentRepository.findAllByContract_ContractNumber("123"))
                .thenReturn(List.of(new Payment(LocalDate.parse("2025-01-01"), BigDecimal.valueOf(1000.00), PaymentType.INCOMING.name(), testContract, testClient)));
        var result = paymentsService.getPaymentsByContractNumber("123");
        assertThat(testContract.getContractNumber()).isEqualTo("123");
        assertThat(result).hasSize(1);
        assertThat(testContract.getClient().getName()).isEqualTo("Client1");
    }

    @Test
    void shouldGetPaymentsByClientId() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(testClient));
        when(paymentRepository.findAllByClient_Id("1"))
                .thenReturn(List.of(new Payment(LocalDate.parse("2025-01-01"), BigDecimal.valueOf(1000.00), PaymentType.INCOMING.name(), testContract, testClient)));
        var result = paymentsService.getPaymentsByClientId("1");
        assertThat(testContract.getContractNumber()).isEqualTo("123");
        assertThat(testContract.getClient().getId()).isEqualTo("1");
        assertThat(result).hasSize(1);
        assertThat(testContract.getClient().getName()).isEqualTo("Client1");
    }

    @Test
    void shouldCreateSinglePayment() {
        when(contractRepository.findByContractNumber("123")).thenReturn(Optional.of(testContract));
        when(clientRepository.findById("1")).thenReturn(Optional.of(testClient));
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(testPayment);
        var result = paymentsService.createPayment(testDomainPayment);
        assertThat(result).isEqualTo(testDomainPayment);
    }

    @Test
    void shouldUploadPaymentsFromCsv() throws JAXBException, IOException {
        String csvContent = "payment_date,amount,type,contract_number\n2024-01-30,1000,incoming,123";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.csv",
                "text/csv",
                csvContent.getBytes()
        );
        when(contractRepository.findByContractNumber("123")).thenReturn(Optional.of(testContract));
        when(clientRepository.findById("1")).thenReturn(Optional.of(testClient));
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(testPayment);
        when(paymentsFileService.processPaymentsFile(file, "1")).thenReturn(List.of(testDomainPayment));
        var result = paymentsService.uploadFile(file, "1");
        assertThat(result.getFirst()).isEqualTo(testDomainPayment);
    }

    @Test
    void shouldUploadPaymentsFromXml() throws JAXBException, IOException {
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
        when(contractRepository.findByContractNumber("123")).thenReturn(Optional.of(testContract));
        when(clientRepository.findById("1")).thenReturn(Optional.of(testClient));
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(testPayment);
        when(paymentsFileService.processPaymentsFile(file, "1")).thenReturn(List.of(testDomainPayment));
        var result = paymentsService.uploadFile(file, "1");
        assertThat(result.getFirst()).isEqualTo(testDomainPayment);
    }

    @Test
    void shouldFailUploadPaymentsFromCsvIfInvalidClient() throws JAXBException, IOException {
        String csvContent = "payment_date,amount,type,contract_number\n2024-01-30,1000,incoming,123";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.csv",
                "text/csv",
                csvContent.getBytes()
        );
        when(contractRepository.findByContractNumber("123")).thenReturn(Optional.of(testContract));
        when(clientRepository.findById("0")).thenReturn(Optional.empty());
        when(paymentsFileService.processPaymentsFile(file, "0")).thenReturn(List.of(testDomainPayment));
        assertThrows(IllegalArgumentException.class, () ->  paymentsService.uploadFile(file, "0"));
    }

    @Test
    void shouldFailUploadPaymentsFromXmlIfInvalidClient() throws JAXBException, IOException {
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
        when(contractRepository.findByContractNumber("123")).thenReturn(Optional.of(testContract));
        when(clientRepository.findById("0")).thenReturn(Optional.empty());
        when(paymentsFileService.processPaymentsFile(file, "0")).thenReturn(List.of(testDomainPayment));
        assertThrows(IllegalArgumentException.class, () ->  paymentsService.uploadFile(file, "0"));
    }

    @Test
    void shouldFailCreateSinglePaymentIfInvalidContractNumber() {
        when(contractRepository.findByContractNumber("123")).thenReturn(Optional.empty());
        when(clientRepository.findById("1")).thenReturn(Optional.of(testClient));
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(testPayment);
        assertThrows(IllegalArgumentException.class, () -> paymentsService.createPayment(testDomainPayment));
    }

    @Test
    void shouldFailCreateSinglePaymentIfInvalidClient() {
        when(contractRepository.findByContractNumber("123")).thenReturn(Optional.of(testContract));
        when(clientRepository.findById("0")).thenReturn(Optional.empty());
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(testPayment);
        assertThrows(IllegalArgumentException.class, () -> paymentsService.createPayment(testDomainPayment));
    }

    @Test
    void shouldFailCreateSinglePaymentIfContractExpired() {
        testContract.setEndDate(LocalDate.now().minusDays(1));
        when(contractRepository.findByContractNumber("123")).thenReturn(Optional.of(testContract));
        when(clientRepository.findById("1")).thenReturn(Optional.of(testClient));
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(testPayment);
        assertThrows(IllegalArgumentException.class, () -> paymentsService.createPayment(testDomainPayment));
    }

    @Test
    void shouldFailCreateSinglePaymentIfContractHasNotStarted() {
        testContract.setStartDate(LocalDate.now().plusYears(1));
        when(contractRepository.findByContractNumber("123")).thenReturn(Optional.of(testContract));
        when(clientRepository.findById("1")).thenReturn(Optional.of(testClient));
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(testPayment);
        assertThrows(IllegalArgumentException.class, () -> paymentsService.createPayment(testDomainPayment));
    }

    @Test
    void shouldFailCreateSinglePaymentIfContractIsInactiveStatus() {
        testContract.setStatus(ContractStatus.INACTIVE.name());
        when(contractRepository.findByContractNumber("123")).thenReturn(Optional.of(testContract));
        when(clientRepository.findById("1")).thenReturn(Optional.of(testClient));
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(testPayment);
        assertThrows(IllegalArgumentException.class, () -> paymentsService.createPayment(testDomainPayment));
    }
}
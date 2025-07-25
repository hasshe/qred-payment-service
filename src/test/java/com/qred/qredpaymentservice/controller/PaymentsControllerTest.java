package com.qred.qredpaymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qred.qredpaymentservice.controller.records.ApiPayment;
import com.qred.qredpaymentservice.service.PaymentsService;
import com.qred.qredpaymentservice.service.domain.DomainPayment;
import com.qred.qredpaymentservice.service.domain.PaymentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentsController.class)
class PaymentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentsService paymentsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getPaymentsByContractNumber() throws Exception {
        var currentDate = LocalDate.now();
        when(paymentsService.getPaymentsByContractNumber("12345")).thenReturn(List.of(new DomainPayment(
                currentDate,
                BigDecimal.valueOf(100L),
                PaymentType.INCOMING,
                "12345",
                "1"
        )));
        mockMvc.perform(get("/api/payments/contract/12345"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].contractNumber").value("12345"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].paymentDate").value(currentDate.toString()))
                .andExpect(jsonPath("$.apiPaymentResponses[0].amount").value("100"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].clientId").value("1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPaymentsByClientId() throws Exception {
        var currentDate = LocalDate.now();
        when(paymentsService.getPaymentsByClientId("1")).thenReturn(List.of(new DomainPayment(
                currentDate,
                BigDecimal.valueOf(100L),
                PaymentType.INCOMING,
                "12345",
                "1"
        )));
        mockMvc.perform(get("/api/payments/client/1"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].contractNumber").value("12345"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].paymentDate").value(currentDate.toString()))
                .andExpect(jsonPath("$.apiPaymentResponses[0].amount").value("100"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].clientId").value("1"))
                .andExpect(status().isOk());
    }

    @Test
    void createIncomingPayment() throws Exception {
        var currentDate = LocalDate.now();
        var domainPayment = new DomainPayment(
                currentDate,
                BigDecimal.valueOf(100L),
                PaymentType.INCOMING,
                "12345",
                "1");
        var apiPayment = new ApiPayment(
                currentDate,
                BigDecimal.valueOf(100L),
                "incoming",
                "12345",
                "1"
        );
        when(paymentsService.createPayment(domainPayment)).thenReturn(domainPayment);
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(apiPayment)))
                .andExpect(jsonPath("$.apiPaymentResponses[0].contractNumber").value("12345"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].paymentDate").value(currentDate.toString()))
                .andExpect(jsonPath("$.apiPaymentResponses[0].amount").value("100"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].clientId").value("1"))
                .andExpect(status().isCreated());
    }

    @Test
    void createOutgoingPayment() throws Exception {
        var currentDate = LocalDate.now();
        var domainPayment = new DomainPayment(
                currentDate,
                BigDecimal.valueOf(100L),
                PaymentType.OUTGOING,
                "12345",
                "1");
        var apiPayment = new ApiPayment(
                currentDate,
                BigDecimal.valueOf(100L),
                "outgoing",
                "12345",
                "1"
        );
        when(paymentsService.createPayment(domainPayment)).thenReturn(domainPayment);
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(apiPayment)))
                .andExpect(jsonPath("$.apiPaymentResponses[0].contractNumber").value("12345"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].paymentDate").value(currentDate.toString()))
                .andExpect(jsonPath("$.apiPaymentResponses[0].amount").value("100"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].clientId").value("1"))
                .andExpect(status().isCreated());
    }

    @Test
    void uploadCsvFile() throws Exception {
        String csvContent = "payment_date,amount,type,contract_number\n2024-01-30,1000,incoming,12345";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.csv",
                "text/csv",
                csvContent.getBytes()
        );
        var domainPayment = new DomainPayment(
                LocalDate.parse("2024-01-30"),
                new BigDecimal("1000"),
                PaymentType.INCOMING,
                "12345",
                "1");
        when(paymentsService.uploadFile(file, "1")).thenReturn(List.of(domainPayment));
        mockMvc.perform(multipart("/api/payments/file/1")
                        .file(file)
                        .param("description", "Test payment CSV file upload")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.apiPaymentResponses[0].contractNumber").value("12345"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].paymentDate").value("2024-01-30"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].amount").value("1000"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].clientId").value("1"));
    }

    @Test
    void uploadXmlFile() throws Exception {
        String xmlContent = """
                <payments>
                    <payment>
                        <payment_date>2024-01-30</payment_date>
                        <amount>1000.00</amount>
                        <type>incoming</type>
                        <contract_number>12345</contract_number>
                    </payment>
                    <payment>
                        <payment_date>2024-01-31</payment_date>
                        <amount>500.00</amount>
                        <type>outgoing</type>
                        <contract_number>54321</contract_number>
                    </payment>
                </payments>
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.xml",
                "application/xml",
                xmlContent.getBytes()
        );

        var domainPayment1 = new DomainPayment(
                LocalDate.parse("2024-01-30"),
                new BigDecimal("1000"),
                PaymentType.INCOMING,
                "12345",
                "1"
        );

        var domainPayment2 = new DomainPayment(
                LocalDate.parse("2024-01-31"),
                new BigDecimal("500"),
                PaymentType.OUTGOING,
                "54321",
                "1"
        );

        when(paymentsService.uploadFile(file, "1")).thenReturn(List.of(domainPayment1, domainPayment2));

        mockMvc.perform(multipart("/api/payments/file/1")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.apiPaymentResponses[0].contractNumber").value("12345"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].paymentDate").value("2024-01-30"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].amount").value("1000"))
                .andExpect(jsonPath("$.apiPaymentResponses[0].clientId").value("1"))
                .andExpect(jsonPath("$.apiPaymentResponses[1].contractNumber").value("54321"))
                .andExpect(jsonPath("$.apiPaymentResponses[1].paymentDate").value("2024-01-31"))
                .andExpect(jsonPath("$.apiPaymentResponses[1].amount").value("500"))
                .andExpect(jsonPath("$.apiPaymentResponses[1].clientId").value("1"));
    }

    @Test
    void failCreatePaymentWithInvalidPaymentType() throws Exception {
        var currentDate = LocalDate.now();
        var domainPayment = new DomainPayment(
                currentDate,
                BigDecimal.valueOf(100L),
                PaymentType.OUTGOING,
                "12345",
                "1");
        var apiPayment = new ApiPayment(
                currentDate,
                BigDecimal.valueOf(100L),
                "something went wrong",
                "12345",
                "1"
        );
        when(paymentsService.createPayment(domainPayment)).thenReturn(domainPayment);
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(apiPayment)))
                .andExpect(status().isBadRequest());
    }

}
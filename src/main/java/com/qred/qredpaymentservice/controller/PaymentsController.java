package com.qred.qredpaymentservice.controller;

import com.qred.qredpaymentservice.controller.records.ApiPayment;
import com.qred.qredpaymentservice.controller.records.response.ListPaymentResponse;
import com.qred.qredpaymentservice.service.PaymentsService;
import com.qred.qredpaymentservice.service.domain.DomainPayment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/payments")
public class PaymentsController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentsController.class);

    private final PaymentsService paymentsService;

    @Autowired
    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    // TODO: Client verification
    @Operation(summary = "Get payments by contract number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved payments"),
            @ApiResponse(responseCode = "404", description = "Payments not found")
    })
    @GetMapping("/contract/{contractNumber}")
    public ResponseEntity<ListPaymentResponse> getPaymentsByContractNumber(@PathVariable String contractNumber) {
        var payments = paymentsService.getPaymentsByContractNumber(contractNumber);
        var responsePayments = payments.stream().map(ApiPayment::fromDomain).toList();
        return ResponseEntity.ok().body(new ListPaymentResponse(responsePayments));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ListPaymentResponse> getPaymentsByClientId(@PathVariable String clientId) {
        var payments = paymentsService.getPaymentsByClientId(clientId);
        var responsePayments = payments.stream().map(ApiPayment::fromDomain).toList();
        return ResponseEntity.ok().body(new ListPaymentResponse(responsePayments));
    }

    @PostMapping
    public ResponseEntity<ApiPayment> createPayment(@Valid @RequestBody ApiPayment requestPayment) {
        logger.info("Create Payment Request");
        var createdPayment = paymentsService.createPayment(DomainPayment.toDomain(requestPayment));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiPayment.fromDomain(createdPayment));
    }

    @PostMapping(path = "/file/{clientId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ListPaymentResponse> uploadFile(@RequestPart("file") MultipartFile file, @PathVariable String clientId) throws IOException, JAXBException {
        logger.info("Upload Payment Request");
        var createdPayments = paymentsService.uploadFile(file, clientId).stream().map(ApiPayment::fromDomain).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(new ListPaymentResponse(createdPayments));
    }
}

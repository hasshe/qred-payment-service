package com.qred.qredpaymentservice.controller;

import com.qred.qredpaymentservice.controller.response.ListPaymentResponse;
import com.qred.qredpaymentservice.controller.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import com.qred.qredpaymentservice.service.PaymentsService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentsController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentsController.class);

    private final PaymentsService paymentsService;

    @Autowired
    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }
    // TODO: GlobalExceptionHandler
    // TODO: Client verification
    @Operation(summary = "Get payments by contract number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved payments"),
            @ApiResponse(responseCode = "404", description = "Payments not found")
    })
    @GetMapping("/contract/{contractNumber}")
    public ResponseEntity<ListPaymentResponse> getPaymentsByContractNumber(@PathVariable String contractNumber) {
        var payments = paymentsService.getPaymentsByContractNumber(contractNumber);
        var responsePayments = payments.stream().map(PaymentResponse::fromDomain).toList();
        return ResponseEntity.ok().body(new ListPaymentResponse(responsePayments));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ListPaymentResponse> getPaymentsByClientId(@PathVariable String clientId) {
        var payments = paymentsService.getPaymentsByClientId(clientId);
        var responsePayments = payments.stream().map(PaymentResponse::fromDomain).toList();
        return ResponseEntity.ok().body(new ListPaymentResponse(responsePayments));
    }

    @PostMapping
    public ResponseEntity<String> createPayment(@Valid @RequestBody String value) {
        logger.info("Create Payment Request");
        var createdPayment = paymentsService.createPayment(value);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @PostMapping("/file")
    public ResponseEntity<List<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("Upload Payment Request");
        var createdPayments = paymentsService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayments);
    }
}

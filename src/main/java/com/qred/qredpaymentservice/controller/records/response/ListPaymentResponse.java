package com.qred.qredpaymentservice.controller.records.response;

import com.qred.qredpaymentservice.controller.records.ApiPayment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.util.List;

public record ListPaymentResponse(@Valid @Schema(description = "List of all payments related to a contract or client") List<ApiPayment> apiPaymentResponses) {
}

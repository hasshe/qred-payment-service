package com.qred.qredpaymentservice.controller.response;

import jakarta.validation.Valid;

import java.util.List;

public record ListPaymentResponse(@Valid List<PaymentResponse> paymentResponses) {
}

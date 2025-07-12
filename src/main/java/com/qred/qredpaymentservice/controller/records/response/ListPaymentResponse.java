package com.qred.qredpaymentservice.controller.records.response;

import com.qred.qredpaymentservice.controller.records.ApiPayment;
import jakarta.validation.Valid;

import java.util.List;

public record ListPaymentResponse(@Valid List<ApiPayment> apiPaymentRespons) {
}

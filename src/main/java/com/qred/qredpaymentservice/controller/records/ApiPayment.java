package com.qred.qredpaymentservice.controller.records;

import com.qred.qredpaymentservice.service.domain.DomainPayment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ApiPayment(@NotNull LocalDate paymentDate, @NotNull BigDecimal amount, @NotBlank String paymentType, @NotBlank String contractNumber, @NotBlank String clientId) {

    public static ApiPayment fromDomain(DomainPayment domainPayment) {
        return new ApiPayment(
                domainPayment.paymentDate(),
                domainPayment.amount(),
                domainPayment.paymentType().toString(),
                domainPayment.contractNumber(),
                domainPayment.clientId()
        );
    }
}

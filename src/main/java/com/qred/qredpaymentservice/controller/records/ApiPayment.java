package com.qred.qredpaymentservice.controller.records;

import com.qred.qredpaymentservice.service.domain.DomainPayment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ApiPayment(
        @NotNull @Schema(description = "Date of the payment being made on the format YYYY-MM-DD") LocalDate paymentDate,
        @NotNull @Schema(description = "Size of the payment") BigDecimal amount,
        @NotBlank @Schema(description = "If the payment is INCOMING or OUTGOING") String paymentType,
        @NotBlank @Schema(description = "The unique identifier of the contract") String contractNumber,
        @NotBlank @Schema(description = "The unique identifier of the client who made/doing the payment") String clientId) {

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

package com.qred.qredpaymentservice.service.domain;

import com.qred.qredpaymentservice.controller.records.ApiPayment;
import com.qred.qredpaymentservice.repository.entities.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DomainPayment(LocalDate paymentDate, BigDecimal amount, PaymentType paymentType, String contractNumber, String clientId) {
    public static DomainPayment toDomain(Payment payment) {
        return new DomainPayment(
                payment.getPaymentDate(),
                payment.getAmount(),
                PaymentType.fromString(payment.getType()),
                payment.getContract().getContractNumber(),
                payment.getClient().getId()
        );
    }

    public static DomainPayment toDomain(ApiPayment payment) {
        return new DomainPayment(
                payment.paymentDate(),
                payment.amount(),
                PaymentType.fromString(payment.paymentType()),
                payment.contractNumber(),
                payment.clientId()
        );
    }
}

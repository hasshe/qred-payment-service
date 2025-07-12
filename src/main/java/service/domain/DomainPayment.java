package service.domain;

import repository.entities.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DomainPayment(LocalDate paymentDate, BigDecimal amount, PaymentType paymentType, String contractNumber, String clientId) {
    public static DomainPayment toDomain(Payment payment) {
        return new DomainPayment(
                payment.getPaymentDate(),
                payment.getAmount(),
                PaymentType.fromString(payment.getType()),
                payment.getContractNumber(),
                payment.getClientId()
        );
    }
}

package controller.response;

import repository.entities.Payment;
import service.domain.DomainPayment;
import service.domain.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponse(LocalDate paymentDate, BigDecimal amount, String paymentType, String contractNumber, String clientId) {

    public static PaymentResponse fromDomain(DomainPayment payment) {
        return new PaymentResponse(
                payment.paymentDate(),
                payment.amount(),
                payment.paymentType().toString(),
                payment.contractNumber(),
                payment.clientId()
        );
    }
}

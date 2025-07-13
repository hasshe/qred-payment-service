package com.qred.qredpaymentservice.service.domain;

public enum PaymentType {
    INCOMING,
    OUTGOING;

    public static PaymentType fromString(String paymentType) {
        return switch (paymentType.toUpperCase()) {
            case "INCOMING" -> INCOMING;
            case "OUTGOING" -> OUTGOING;
            default -> throw new IllegalArgumentException("Invalid PaymentType: " + paymentType);
        };
    }
}

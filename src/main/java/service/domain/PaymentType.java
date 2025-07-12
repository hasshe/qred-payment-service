package service.domain;

import utils.exceptions.PaymentTypeException;

public enum PaymentType {
    INCOMING,
    OUTGOING;

    public static PaymentType fromString(String paymentType) {
        return switch (paymentType.toUpperCase()) {
            case "INCOMING"-> INCOMING;
            case "OUTGOING" -> OUTGOING;
            default -> throw new PaymentTypeException("Invalid PaymentType: " + paymentType);
        };
    }
}

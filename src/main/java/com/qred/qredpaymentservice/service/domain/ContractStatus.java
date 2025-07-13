package com.qred.qredpaymentservice.service.domain;

public enum ContractStatus {
    ACTIVE, INACTIVE;

    public static ContractStatus fromString(String contractStatus) {
        return switch (contractStatus.toUpperCase()) {
            case "ACTIVE" -> ACTIVE;
            case "INACTIVE" -> INACTIVE;
            default -> throw new IllegalArgumentException("Invalid Contract Status: " + contractStatus);
        };
    }
}

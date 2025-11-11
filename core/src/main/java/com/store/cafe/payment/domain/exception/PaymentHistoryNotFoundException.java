package com.store.cafe.payment.domain.exception;

public class PaymentHistoryNotFoundException extends RuntimeException {
    public PaymentHistoryNotFoundException(String message) {
        super(message);
    }
}

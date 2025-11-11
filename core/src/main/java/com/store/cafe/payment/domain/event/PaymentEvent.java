package com.store.cafe.payment.domain.event;

import com.store.cafe.payment.domain.enums.PaymentStatus;

public record PaymentEvent(
        Long orderId,
        String transactionId,
        PaymentStatus status
) {

    public static PaymentEvent of(Long orderId, String transactionId, PaymentStatus status) {
        return new PaymentEvent(orderId, transactionId, status);
    }
}

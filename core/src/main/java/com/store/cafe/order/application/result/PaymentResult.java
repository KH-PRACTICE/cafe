package com.store.cafe.order.application.result;

import com.store.cafe.payment.domain.enums.PaymentStatus;

public record PaymentResult(
        String transactionId,
        Long orderId,
        PaymentStatus status
) {

    public static PaymentResult success(String transactionId, Long orderId) {
        return new PaymentResult(transactionId, orderId, PaymentStatus.PAYMENT_SUCCESS);
    }

    public static PaymentResult failure(String transactionId, Long orderId) {
        return new PaymentResult(transactionId, orderId, PaymentStatus.PAYMENT_FAILED);
    }

    public static PaymentResult cancelled(String transactionId, Long orderId) {
        return new PaymentResult(transactionId, orderId, PaymentStatus.PAYMENT_CANCELLED);
    }

    public boolean isPaymentSuccess() {
        return this.status == PaymentStatus.PAYMENT_SUCCESS;
    }

    public boolean isPaymentFailed() {
        return this.status == PaymentStatus.PAYMENT_FAILED;
    }

}
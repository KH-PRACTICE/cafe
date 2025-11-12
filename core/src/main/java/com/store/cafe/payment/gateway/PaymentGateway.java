package com.store.cafe.payment.gateway;

public interface PaymentGateway {

    PaymentResult processPayment(Long orderId);

    PaymentResult cancelPayment(String transactionId, Long orderId);
}
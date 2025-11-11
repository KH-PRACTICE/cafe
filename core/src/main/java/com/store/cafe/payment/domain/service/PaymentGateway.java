package com.store.cafe.payment.domain.service;

import com.store.cafe.order.application.result.PaymentResult;

public interface PaymentGateway {

    PaymentResult processPayment(Long orderId);

    PaymentResult cancelPayment(String transactionId, Long orderId);
}
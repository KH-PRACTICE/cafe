package com.store.cafe.payment.domain.service;

import com.store.cafe.order.application.result.PaymentResult;
import com.store.cafe.payment.domain.enums.PaymentStatus;

public interface PaymentGateway {

    PaymentResult processPayment(Long orderId);

    PaymentResult cancelPayment(String transactionId, Long orderId);

    PaymentStatus getPaymentStatus(Long orderId);
}
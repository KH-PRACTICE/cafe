package com.store.cafe.payment.domain.model.entity;

import java.util.Optional;

public interface PaymentOrderRepository {

    PaymentOrderHistory save(PaymentOrderHistory PaymentOrderHistory);

    Optional<PaymentOrderHistory> findByTransactionId(String transactionId);

    Optional<PaymentOrderHistory> findByOrderId(Long orderId);
}
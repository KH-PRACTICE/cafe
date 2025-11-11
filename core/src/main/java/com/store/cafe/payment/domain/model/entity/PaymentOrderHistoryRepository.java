package com.store.cafe.payment.domain.model.entity;

import com.store.cafe.payment.domain.enums.PaymentStatus;

import java.util.Optional;

public interface PaymentOrderHistoryRepository {

    PaymentOrderHistory save(PaymentOrderHistory PaymentOrderHistory);

    Optional<PaymentOrderHistory> findByOrderIdAndStatus(Long orderId, PaymentStatus paymentStatus);
}
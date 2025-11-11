package com.store.cafe.order.domain.model.entity;

import java.util.Optional;

public interface PaymentOrderRepository {

    PaymentOrder save(PaymentOrder PaymentOrder);

    Optional<PaymentOrder> findByPaymentId(Long paymentId);
    
    Optional<PaymentOrder> findByOrderId(Long orderId);
}
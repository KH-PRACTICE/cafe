package com.store.cafe.order;

import com.store.cafe.order.domain.model.entity.PaymentOrder;
import com.store.cafe.order.domain.model.entity.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentOrderRepositoryImpl implements PaymentOrderRepository {

    private final PaymentOrderJpaRepository jpaRepository;

    @Override
    public PaymentOrder save(PaymentOrder PaymentOrder) {
        return jpaRepository.save(PaymentOrder);
    }

    @Override
    public Optional<PaymentOrder> findByPaymentId(Long paymentId) {
        return jpaRepository.findById(paymentId);
    }

    @Override
    public Optional<PaymentOrder> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId);
    }
}
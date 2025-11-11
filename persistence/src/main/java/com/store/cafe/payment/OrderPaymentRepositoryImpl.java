package com.store.cafe.payment;

import com.store.cafe.payment.domain.model.entity.PaymentOrderHistory;
import com.store.cafe.payment.domain.model.entity.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentOrderRepositoryImpl implements PaymentOrderRepository {

    private final PaymentOrderJpaRepository jpaRepository;

    @Override
    public PaymentOrderHistory save(PaymentOrderHistory PaymentOrderHistory) {
        return jpaRepository.save(PaymentOrderHistory);
    }

    @Override
    public Optional<PaymentOrderHistory> findByTransactionId(String transactionId) {

    }

    @Override
    public Optional<PaymentOrderHistory> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId);
    }
}
package com.store.cafe.payment;

import com.store.cafe.payment.domain.enums.PaymentStatus;
import com.store.cafe.payment.domain.model.entity.PaymentOrderHistory;
import com.store.cafe.payment.domain.model.entity.PaymentOrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentOrderHistoryRepositoryImpl implements PaymentOrderHistoryRepository {

    private final PaymentOrderJpaRepository jpaRepository;

    @Override
    public PaymentOrderHistory save(PaymentOrderHistory PaymentOrderHistory) {
        return jpaRepository.save(PaymentOrderHistory);
    }

    @Override
    public Optional<PaymentOrderHistory> findByOrderIdAndStatus(Long orderId, PaymentStatus paymentStatus) {
        return jpaRepository.findByOrderIdAndStatus(orderId, paymentStatus);
    }
}
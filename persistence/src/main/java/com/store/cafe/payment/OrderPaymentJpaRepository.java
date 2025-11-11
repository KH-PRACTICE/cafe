package com.store.cafe.payment;

import com.store.cafe.payment.domain.model.entity.PaymentOrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentOrderJpaRepository extends JpaRepository<PaymentOrderHistory, Long> {

    Optional<PaymentOrderHistory> findByOrderId(Long orderId);
}
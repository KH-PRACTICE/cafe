package com.store.cafe.payment.domain.service;

import com.store.cafe.payment.domain.enums.PaymentStatus;
import com.store.cafe.payment.domain.exception.PaymentHistoryNotFoundException;
import com.store.cafe.payment.domain.model.entity.PaymentOrderHistory;
import com.store.cafe.payment.domain.model.entity.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final PaymentOrderRepository PaymentOrderRepository;

    @Transactional
    public void savePaymentOrder(Long orderId, String transactionId, PaymentStatus status) {

        PaymentOrderHistory paymentOrderHistory = PaymentOrderHistory.of(
                orderId,
                transactionId,
                status
        );

        PaymentOrderRepository.save(paymentOrderHistory);
    }

    public PaymentOrderHistory getPaymentOrder(Long orderId) {
        return PaymentOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentHistoryNotFoundException("존재 하지 않는 결제 내역 입니다: " + orderId));
    }
}

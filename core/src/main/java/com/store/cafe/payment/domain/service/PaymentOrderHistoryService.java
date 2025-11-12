package com.store.cafe.payment.domain.service;

import com.store.cafe.payment.domain.enums.PaymentStatus;
import com.store.cafe.payment.domain.exception.PaymentHistoryNotFoundException;
import com.store.cafe.payment.domain.model.entity.PaymentOrderHistory;
import com.store.cafe.payment.domain.model.entity.PaymentOrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentOrderHistoryService {

    private final PaymentOrderHistoryRepository PaymentOrderRepository;

    @Transactional
    public void savePaymentOrder(Long orderId, String transactionId, PaymentStatus status) {

        PaymentOrderHistory paymentOrderHistory = PaymentOrderHistory.of(
                orderId,
                transactionId,
                status
        );

        PaymentOrderRepository.save(paymentOrderHistory);
    }

    public PaymentOrderHistory getSuccessPaymentOrder(Long orderId) {
        return PaymentOrderRepository.findByOrderIdAndStatus(orderId, PaymentStatus.PAYMENT_SUCCESS)
                .orElseThrow(() -> new PaymentHistoryNotFoundException("Not Found Successful Payment History. orderId=" + orderId));
    }
}

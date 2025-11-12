package com.store.cafe.payment.gateway;

import com.store.cafe.order.application.result.PaymentResult;
import com.store.cafe.payment.domain.service.PaymentGateway;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MockPaymentGateway implements PaymentGateway {

    private final Random random = new Random();

    /**
     * 결제 요청은 50% 확률로 성공/실패 처리
     */
    @Override
    public PaymentResult processPayment(Long orderId) {
        try {
            Thread.sleep((long) (Math.random() * 1000));

            if (random.nextBoolean()) {
                throw new Exception("payment failed!");
            }

            return PaymentResult.success(buildTransactionId(orderId), orderId);
        } catch (Exception e) {
            return PaymentResult.failure(buildTransactionId(orderId), orderId);
        }
    }

    /**
     * 주문 취소 요청은 100% 성공한다고 가정
     */
    @Override
    public PaymentResult cancelPayment(String transactionId, Long orderId) {
        return PaymentResult.cancelled(transactionId, orderId);
    }

    private String buildTransactionId(Long orderId) {
        return "TXN-" + orderId;
    }
}
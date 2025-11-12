package com.store.cafe.payment.gateway;

import com.store.cafe.order.application.result.PaymentResult;
import com.store.cafe.payment.domain.service.PaymentGateway;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 통합 테스트를 위한 PaymentGateway 구현체
 * 결제 성공 및 실패를 강제할 수 있는 기능을 제공합니다.
 */
@Component
@Primary  // 테스트 환경에서 이 빈이 우선적으로 사용됨
public class TestPaymentGateway implements PaymentGateway {

    private static final ThreadLocal<Boolean> forcePaymentSuccess = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> forcePaymentFailure = new ThreadLocal<>();

    /**
     * 결제 성공을 강제
     */
    public void forceSuccess() {
        forcePaymentSuccess.set(true);
        forcePaymentFailure.set(false);
    }

    /**
     * 결제 실패를 강제
     */
    public void forceFailure() {
        forcePaymentSuccess.set(false);
        forcePaymentFailure.set(true);
    }

    /**
     * 강제 설정을 초기화하고 기본 동작(성공)으로 되돌립니다.
     */
    public void reset() {
        forcePaymentSuccess.remove();
        forcePaymentFailure.remove();
    }

    @Override
    public PaymentResult processPayment(Long orderId) {
        Boolean forcedSuccess = forcePaymentSuccess.get();
        Boolean forcedFailure = forcePaymentFailure.get();

        if (Boolean.TRUE.equals(forcedSuccess)) {
            return PaymentResult.success(buildTransactionId(orderId), orderId);
        }

        if (Boolean.TRUE.equals(forcedFailure)) {
            return PaymentResult.failure(buildTransactionId(orderId), orderId);
        }

        return PaymentResult.success(buildTransactionId(orderId), orderId);
    }

    @Override
    public PaymentResult cancelPayment(String transactionId, Long orderId) {
        return PaymentResult.cancelled(transactionId, orderId);
    }

    private String buildTransactionId(Long orderId) {
        return "TXN-" + orderId;
    }
}
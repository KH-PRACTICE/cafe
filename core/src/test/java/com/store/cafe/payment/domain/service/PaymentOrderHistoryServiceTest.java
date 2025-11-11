package com.store.cafe.payment.domain.service;

import com.store.cafe.payment.domain.enums.PaymentStatus;
import com.store.cafe.payment.domain.exception.PaymentHistoryNotFoundException;
import com.store.cafe.payment.domain.model.entity.PaymentOrderHistory;
import com.store.cafe.payment.domain.model.entity.PaymentOrderHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PaymentHistoryServiceTest {

    @Mock
    private PaymentOrderHistoryRepository paymentOrderRepository;

    @InjectMocks
    private PaymentOrderHistoryService paymentHistoryService;

    @Test
    @DisplayName("결제 주문 이력을 저장한다")
    void savePaymentOrder() {
        // given
        Long orderId = 1L;
        String transactionId = "TXN -123456";
        PaymentStatus status = PaymentStatus.PAYMENT_SUCCESS;

        // when
        paymentHistoryService.savePaymentOrder(orderId, transactionId, status);

        // then
        then(paymentOrderRepository).should(times(1)).save(any(PaymentOrderHistory.class));
    }

    @Test
    @DisplayName("주문 ID로 성공한 결제 이력을 조회한다")
    void getSuccessPaymentOrder_Success() {
        // given
        Long orderId = 1L;
        String transactionId = "TXN -123456";
        PaymentStatus status = PaymentStatus.PAYMENT_SUCCESS;

        PaymentOrderHistory paymentOrderHistory = PaymentOrderHistory.of(
                orderId,
                transactionId,
                status
        );
        setPaymentId(paymentOrderHistory, 1L);

        given(paymentOrderRepository.findByOrderIdAndStatus(eq(orderId), eq(PaymentStatus.PAYMENT_SUCCESS)))
                .willReturn(Optional.of(paymentOrderHistory));

        // when
        PaymentOrderHistory result = paymentHistoryService.getSuccessPaymentOrder(orderId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getTransactionId()).isEqualTo(transactionId);
        assertThat(result.getStatus()).isEqualTo(status);

        then(paymentOrderRepository).should(times(1))
                .findByOrderIdAndStatus(eq(orderId), eq(PaymentStatus.PAYMENT_SUCCESS));
    }

    @Test
    @DisplayName("성공한 결제 이력이 없으면 예외가 발생한다")
    void getSuccessPaymentOrder_NotFound_ThrowsException() {
        // given
        Long orderId = 999L;

        given(paymentOrderRepository.findByOrderIdAndStatus(eq(orderId), eq(PaymentStatus.PAYMENT_SUCCESS)))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentHistoryService.getSuccessPaymentOrder(orderId))
                .isInstanceOf(PaymentHistoryNotFoundException.class)
                .hasMessageContaining("존재 하지 않는 결제 내역 입니다: " + orderId);

        then(paymentOrderRepository).should(times(1))
                .findByOrderIdAndStatus(eq(orderId), eq(PaymentStatus.PAYMENT_SUCCESS));
    }

    private void setPaymentId(PaymentOrderHistory paymentOrderHistory, Long paymentId) {
        try {
            var field = PaymentOrderHistory.class.getDeclaredField("paymentId");
            field.setAccessible(true);
            field.set(paymentOrderHistory, paymentId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set paymentId", e);
        }
    }
}
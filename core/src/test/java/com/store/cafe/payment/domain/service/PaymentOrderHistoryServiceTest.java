package com.store.cafe.payment.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.store.cafe.payment.domain.enums.PaymentStatus;
import com.store.cafe.payment.domain.exception.PaymentHistoryNotFoundException;
import com.store.cafe.payment.domain.model.entity.PaymentOrderHistory;
import com.store.cafe.payment.domain.model.entity.PaymentOrderHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentOrderHistoryServiceTest {

    @Mock
    private PaymentOrderHistoryRepository paymentOrderRepository;

    @InjectMocks
    private PaymentOrderHistoryService paymentOrderHistoryService;

    private Long testOrderId;
    private String testTransactionId;
    private PaymentOrderHistory testPaymentOrderHistory;

    @BeforeEach
    void setUp() {
        testOrderId = 100L;
        testTransactionId = "TXN-100";
        testPaymentOrderHistory = PaymentOrderHistory.of(testOrderId, testTransactionId, PaymentStatus.PAYMENT_SUCCESS);
    }

    @Test
    @DisplayName("savePaymentOrder() - 결제 이력을 성공적으로 저장한다")
    void savePaymentOrder_success() {
        // given
        PaymentStatus status = PaymentStatus.PAYMENT_SUCCESS;
        
        // when
        paymentOrderHistoryService.savePaymentOrder(testOrderId, testTransactionId, status);

        // then
        verify(paymentOrderRepository).save(any(PaymentOrderHistory.class));
    }

    @Test
    @DisplayName("getSuccessPaymentOrder() - 성공한 결제 이력을 조회한다")
    void getSuccessPaymentOrder_success() {
        // given
        when(paymentOrderRepository.findByOrderIdAndStatus(testOrderId, PaymentStatus.PAYMENT_SUCCESS))
                .thenReturn(Optional.of(testPaymentOrderHistory));

        // when
        PaymentOrderHistory result = paymentOrderHistoryService.getSuccessPaymentOrder(testOrderId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(testOrderId);
        assertThat(result.getTransactionId()).isEqualTo(testTransactionId);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAYMENT_SUCCESS);
        verify(paymentOrderRepository).findByOrderIdAndStatus(testOrderId, PaymentStatus.PAYMENT_SUCCESS);
    }

    @Test
    @DisplayName("getSuccessPaymentOrder() - 존재하지 않는 결제 이력 조회 시 예외가 발생한다")
    void getSuccessPaymentOrder_notFound() {
        // given
        when(paymentOrderRepository.findByOrderIdAndStatus(testOrderId, PaymentStatus.PAYMENT_SUCCESS))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentOrderHistoryService.getSuccessPaymentOrder(testOrderId))
                .isInstanceOf(PaymentHistoryNotFoundException.class)
                .hasMessageContaining("존재 하지 않는 결제 내역 입니다: " + testOrderId);
        
        verify(paymentOrderRepository).findByOrderIdAndStatus(testOrderId, PaymentStatus.PAYMENT_SUCCESS);
    }
}
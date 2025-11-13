package com.store.cafe.order.application.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.store.cafe.order.application.command.OrderCommand;
import com.store.cafe.order.application.command.OrderItemCommand;
import com.store.cafe.order.application.result.OrderCancelResult;
import com.store.cafe.order.application.result.OrderResult;
import com.store.cafe.order.domain.enums.OrderStatus;
import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.service.OrderCancelService;
import com.store.cafe.order.domain.service.OrderTransactionService;
import com.store.cafe.payment.domain.enums.PaymentStatus;
import com.store.cafe.payment.domain.model.entity.PaymentOrderHistory;
import com.store.cafe.payment.domain.service.PaymentOrderHistoryService;
import com.store.cafe.payment.gateway.PaymentGateway;
import com.store.cafe.payment.gateway.PaymentResult;
import com.store.cafe.product.domain.model.vo.PricedOrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderFacadeImplTest {

    @Mock
    private OrderTransactionService orderTransactionService;

    @Mock
    private OrderCancelService orderCancelService;

    @Mock
    private PaymentOrderHistoryService paymentOrderHistoryService;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private OrderFacadeImpl orderFacade;

    private Long testMemberUid;
    private Long testOrderId;
    private String testTransactionId;
    private OrderCommand testOrderCommand;
    private Order testOrder;
    private PaymentResult testSuccessPaymentResult;
    private PaymentResult testFailurePaymentResult;
    private PaymentOrderHistory testPaymentOrderHistory;

    @BeforeEach
    void setUp() {
        testMemberUid = 100L;
        testOrderId = 1L;
        testTransactionId = "TXN-100";

        List<OrderItemCommand> items = List.of(
            new OrderItemCommand(1L, 2L),
            new OrderItemCommand(2L, 1L)
        );
        testOrderCommand = new OrderCommand(testMemberUid, items);

        List<PricedOrderItem> pricedItems = List.of(
            new PricedOrderItem(1L, 2L, 5000L),
            new PricedOrderItem(2L, 1L, 4500L)
        );
        testOrder = Order.create(testMemberUid, pricedItems);

        testSuccessPaymentResult = PaymentResult.success(testTransactionId, testOrderId);
        testFailurePaymentResult = PaymentResult.failure(testTransactionId, testOrderId);

        testPaymentOrderHistory = PaymentOrderHistory.of(testOrderId, testTransactionId, PaymentStatus.PAYMENT_SUCCESS);
    }

    @Test
    @DisplayName("order() - 결제 성공 시 주문이 성공적으로 처리된다")
    void order_paymentSuccess() {
        // given
        Order completedOrder = Order.create(testMemberUid, List.of());
        completedOrder.complete();

        when(orderTransactionService.createOrderWithStockDecrease(testMemberUid, testOrderCommand.items()))
                .thenReturn(testOrder);
        when(paymentGateway.processPayment(testOrder.getOrderId()))
                .thenReturn(testSuccessPaymentResult);
        when(orderTransactionService.completeOrder(testOrderId))
                .thenReturn(completedOrder);

        // when
        OrderResult result = orderFacade.order(testOrderCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(completedOrder.getOrderId());
        assertThat(result.status()).isEqualTo(OrderStatus.COMPLETED);

        verify(orderTransactionService).createOrderWithStockDecrease(testMemberUid, testOrderCommand.items());
        verify(paymentGateway).processPayment(testOrder.getOrderId());
        verify(paymentOrderHistoryService).savePaymentOrder(testOrderId, testTransactionId, PaymentStatus.PAYMENT_SUCCESS);
        verify(orderTransactionService).completeOrder(testOrderId);
    }

    @Test
    @DisplayName("order() - 결제 실패 시 주문이 실패 처리된다")
    void order_paymentFailure() {
        // given
        Order failedOrder = Order.create(testMemberUid, List.of());
        failedOrder.fail();

        when(orderTransactionService.createOrderWithStockDecrease(testMemberUid, testOrderCommand.items()))
                .thenReturn(testOrder);
        when(paymentGateway.processPayment(testOrder.getOrderId()))
                .thenReturn(testFailurePaymentResult);
        when(orderTransactionService.failOrder(testOrderId))
                .thenReturn(failedOrder);

        // when
        OrderResult result = orderFacade.order(testOrderCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(failedOrder.getOrderId());
        assertThat(result.status()).isEqualTo(OrderStatus.FAILED);

        verify(orderTransactionService).createOrderWithStockDecrease(testMemberUid, testOrderCommand.items());
        verify(paymentGateway).processPayment(testOrder.getOrderId());
        verify(paymentOrderHistoryService).savePaymentOrder(testOrderId, testTransactionId, PaymentStatus.PAYMENT_FAILED);
        verify(orderTransactionService).failOrder(testOrderId);
    }

    @Test
    @DisplayName("cancelOrder() - 주문 취소가 성공적으로 처리된다")
    void cancelOrder_success() {
        // given
        Order canceledOrder = Order.create(testMemberUid, List.of());
        canceledOrder.complete();
        canceledOrder.cancel();

        PaymentResult cancelPaymentResult = PaymentResult.cancelled(testTransactionId, testOrderId);

        when(paymentOrderHistoryService.getSuccessPaymentOrder(testOrderId))
                .thenReturn(testPaymentOrderHistory);
        when(paymentGateway.cancelPayment(testTransactionId, testOrderId))
                .thenReturn(cancelPaymentResult);
        when(orderCancelService.cancelOrder(testOrderId))
                .thenReturn(canceledOrder);

        // when
        OrderCancelResult result = orderFacade.cancelOrder(testOrderId, testMemberUid);

        // then
        assertThat(result).isNotNull();
        assertThat(result.success()).isTrue();
        assertThat(result.canceledAt()).isNotNull();

        verify(orderCancelService).validateCancellable(testOrderId, testMemberUid);
        verify(paymentOrderHistoryService).getSuccessPaymentOrder(testOrderId);
        verify(paymentGateway).cancelPayment(testTransactionId, testOrderId);
        verify(paymentOrderHistoryService).savePaymentOrder(testOrderId, testTransactionId, PaymentStatus.PAYMENT_CANCELLED);
        verify(orderCancelService).cancelOrder(testOrderId);
    }

    @Test
    @DisplayName("cancelOrder() - 결제 취소 실패 시 주문 취소가 실패한다")
    void cancelOrder_paymentCancelFailure() {
        // given
        PaymentResult failedCancelResult = PaymentResult.failure(testTransactionId, testOrderId);

        when(paymentOrderHistoryService.getSuccessPaymentOrder(testOrderId))
                .thenReturn(testPaymentOrderHistory);
        when(paymentGateway.cancelPayment(testTransactionId, testOrderId))
                .thenReturn(failedCancelResult);

        // when
        OrderCancelResult result = orderFacade.cancelOrder(testOrderId, testMemberUid);

        // then
        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(testOrderId);
        assertThat(result.success()).isFalse();
        assertThat(result.canceledAt()).isNull();

        verify(orderCancelService).validateCancellable(testOrderId, testMemberUid);
        verify(paymentOrderHistoryService).getSuccessPaymentOrder(testOrderId);
        verify(paymentGateway).cancelPayment(testTransactionId, testOrderId);
        verify(paymentOrderHistoryService).savePaymentOrder(testOrderId, testTransactionId, PaymentStatus.PAYMENT_FAILED);
        // cancelOrder 메서드는 호출되지 않아야 함
    }
}
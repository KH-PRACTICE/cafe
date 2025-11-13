package com.store.cafe.order.application.facade;

import com.store.cafe.order.application.command.OrderCommand;
import com.store.cafe.order.application.result.OrderCancelResult;
import com.store.cafe.order.application.result.OrderResult;
import com.store.cafe.payment.gateway.PaymentResult;
import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.service.OrderCancelService;
import com.store.cafe.order.domain.service.OrderTransactionService;
import com.store.cafe.payment.domain.model.entity.PaymentOrderHistory;
import com.store.cafe.payment.gateway.PaymentGateway;
import com.store.cafe.payment.domain.service.PaymentOrderHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacadeImpl implements OrderFacade {

    private final OrderTransactionService orderTransactionService;
    private final OrderCancelService orderCancelService;

    private final PaymentOrderHistoryService paymentOrderHistoryService;

    private final PaymentGateway paymentGateway;

    @Override
    public OrderResult order(OrderCommand command) {

        Order createdOrder = orderTransactionService.createOrderWithStockDecrease(
                command.memberUid(),
                command.items()
        );

        PaymentResult paymentResult = paymentGateway.processPayment(createdOrder.getOrderId());

        savePaymentOrderHistory(paymentResult);

        Order finalOrder = paymentResult.isPaymentSuccess()
                ? orderTransactionService.completeOrder(paymentResult.orderId())
                : orderTransactionService.failOrder(paymentResult.orderId());

        return OrderResult.from(finalOrder, paymentResult.isPaymentSuccess());
    }

    private void savePaymentOrderHistory(PaymentResult paymentResult) {
        paymentOrderHistoryService.savePaymentOrder(
                paymentResult.orderId(),
                paymentResult.transactionId(),
                paymentResult.status()
        );
    }

    @Override
    public OrderCancelResult cancelOrder(Long orderId, Long memberUid) {

        orderCancelService.validateCancellable(orderId, memberUid);

        PaymentOrderHistory paymentOrderHistory = paymentOrderHistoryService.getSuccessPaymentOrder(orderId);
        PaymentResult paymentResult = paymentGateway.cancelPayment(paymentOrderHistory.getTransactionId(), paymentOrderHistory.getOrderId());

        savePaymentOrderHistory(paymentResult);

        if (paymentResult.isPaymentFailed()) {
            return OrderCancelResult.failure(orderId);
        }

        Order canceledOrder = orderCancelService.cancelOrder(orderId);

        return OrderCancelResult.success(
                canceledOrder.getOrderId(),
                canceledOrder.getCanceledAt()
        );
    }
}
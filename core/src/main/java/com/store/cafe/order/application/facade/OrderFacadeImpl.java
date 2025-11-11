package com.store.cafe.order.application.facade;

import com.store.cafe.order.application.command.OrderCommand;
import com.store.cafe.order.application.result.OrderCancelResult;
import com.store.cafe.order.application.result.OrderResult;
import com.store.cafe.order.application.result.PaymentResult;
import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.service.OrderCancelService;
import com.store.cafe.order.domain.service.OrderTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacadeImpl implements OrderFacade {

    private final OrderTransactionService orderTransactionService;
    private final OrderCancelService orderCancelService;

    @Override
    public OrderResult order(OrderCommand command) {

        Order createdOrder = orderTransactionService.createOrderWithStockDecrease(
                command.memberUid(),
                command.items()
        );

        PaymentResult paymentResult = orderTransactionService.requestPayment(createdOrder.getOrderId());

        Order finalOrder = paymentResult.isPaymentSuccess()
                ? orderTransactionService.completeOrder(createdOrder.getOrderId())
                : orderTransactionService.failOrder(createdOrder.getOrderId());

        return OrderResult.from(finalOrder, paymentResult.isPaymentSuccess());
    }

    @Override
    public OrderCancelResult cancelOrder(Long orderId, Long memberUid) {

        orderCancelService.validateCancellable(orderId, memberUid);

        PaymentResult paymentResult = orderCancelService.requestPaymentCancel(orderId);

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
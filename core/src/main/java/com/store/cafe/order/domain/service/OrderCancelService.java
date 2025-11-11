package com.store.cafe.order.domain.service;

import com.store.cafe.order.application.result.PaymentResult;
import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.model.entity.OrderItem;
import com.store.cafe.payment.domain.event.PaymentEvent;
import com.store.cafe.payment.domain.model.entity.PaymentOrderHistory;
import com.store.cafe.payment.domain.service.PaymentGateway;
import com.store.cafe.payment.domain.service.PaymentHistoryService;
import com.store.cafe.product.domain.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCancelService {

    private final OrderReadService orderReadService;
    private final ProductStockService productStockService;
    private final PaymentHistoryService paymentHistoryService;
    private final PaymentGateway paymentGateway;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Order cancelOrder(Long orderId) {

        List<OrderItem> orderItems = orderReadService.getOrderItems(orderId);
        productStockService.restoreStocks(orderItems);

        Order order = orderReadService.getOrder(orderId);
        order.cancel();

        return order;
    }

    public void validateCancellable(Long orderId, Long memberUid) {
        Order order = orderReadService.getOrder(orderId);
        order.validateCancellable(memberUid);
    }

    public PaymentResult requestPaymentCancel(Long orderId) {

        PaymentOrderHistory paymentOrderHistory = paymentHistoryService.getSuccessPaymentOrder(orderId);

        PaymentResult paymentResult = paymentGateway.cancelPayment(paymentOrderHistory.getTransactionId(), paymentOrderHistory.getOrderId());

        eventPublisher.publishEvent(PaymentEvent.of(
                        paymentResult.orderId(),
                        paymentResult.transactionId(),
                        paymentResult.status()
                )
        );

        return paymentResult;
    }
}
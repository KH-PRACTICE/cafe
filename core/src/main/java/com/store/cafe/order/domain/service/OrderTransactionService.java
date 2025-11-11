package com.store.cafe.order.domain.service;

import com.store.cafe.order.application.command.OrderItemCommand;
import com.store.cafe.order.application.result.PaymentResult;
import com.store.cafe.order.domain.exception.OrderItemNotFoundException;
import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.model.entity.OrderItem;
import com.store.cafe.payment.domain.event.PaymentEvent;
import com.store.cafe.payment.domain.service.PaymentGateway;
import com.store.cafe.product.domain.model.vo.PricedOrderItem;
import com.store.cafe.product.domain.model.vo.StockDecreaseResult;
import com.store.cafe.product.domain.service.ProductReadService;
import com.store.cafe.product.domain.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderTransactionService {

    private final OrderCreateService orderCreateService;
    private final OrderReadService orderReadService;
    private final ProductStockService productStockService;
    private final ProductReadService productReadService;

    private final PaymentGateway paymentGateway;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Order createOrderWithStockDecrease(Long memberUid, List<OrderItemCommand> items) {

        List<StockDecreaseResult> stockResults = productStockService.decreaseStocks(items);
        List<PricedOrderItem> pricedItems = productReadService.attachPrices(stockResults);

        return orderCreateService.createOrder(memberUid, pricedItems);
    }

    public PaymentResult requestPayment(Long orderId) {

        PaymentResult paymentResult = paymentGateway.processPayment(orderId);

        eventPublisher.publishEvent(PaymentEvent.of(
                        paymentResult.orderId(),
                        paymentResult.transactionId(),
                        paymentResult.status()
                )
        );

        return paymentResult;
    }

    @Transactional
    public Order completeOrder(Long orderId) {
        Order order = orderReadService.getOrder(orderId);
        order.complete();

        return order;
    }

    @Transactional
    public Order failOrder(Long orderId) {

        List<OrderItem> orderItems = orderReadService.getOrderItems(orderId);
        productStockService.restoreStocks(orderItems);

        Order order = orderReadService.getOrder(orderId);
        order.fail();

        return order;
    }

}

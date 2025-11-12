package com.store.cafe.order.domain.service;

import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.model.entity.OrderItem;
import com.store.cafe.product.domain.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCancelService {

    private final OrderReadService orderReadService;
    private final ProductStockService productStockService;

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
}
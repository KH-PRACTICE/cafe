package com.store.cafe.order.domain.service;

import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.model.entity.OrderItem;
import com.store.cafe.order.domain.model.entity.OrderItemRepository;
import com.store.cafe.order.domain.model.entity.OrderRepository;
import com.store.cafe.product.domain.model.vo.PricedOrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCreateService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Order createOrder(Long memberUid, List<PricedOrderItem> pricedItems) {

        Order savedOrder = orderRepository.save(Order.create(memberUid, pricedItems));
        saveOrderItems(savedOrder.getOrderId(), pricedItems);

        return savedOrder;
    }

    private void saveOrderItems(Long orderId, List<PricedOrderItem> pricedItems) {

        List<OrderItem> orderItems = pricedItems.stream()
                .map(pricedItem -> OrderItem.of(
                        orderId,
                        pricedItem.productId(),
                        pricedItem.quantity(),
                        pricedItem.price()
                ))
                .toList();

        orderItemRepository.saveAll(orderItems);
    }
}

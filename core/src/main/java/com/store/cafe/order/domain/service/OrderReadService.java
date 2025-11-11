package com.store.cafe.order.domain.service;

import com.store.cafe.order.domain.exception.OrderNotFoundException;
import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.model.entity.OrderItem;
import com.store.cafe.order.domain.model.entity.OrderItemRepository;
import com.store.cafe.order.domain.model.entity.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReadService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public Order getOrder(Long orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException("존재 하지 않는 주문 입니다: " + orderId));
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}

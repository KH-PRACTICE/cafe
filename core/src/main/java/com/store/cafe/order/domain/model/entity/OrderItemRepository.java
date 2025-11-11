package com.store.cafe.order.domain.model.entity;

import java.util.List;

public interface OrderItemRepository {

    OrderItem save(OrderItem orderItem);

    void saveAll(List<OrderItem> orderItems);

    List<OrderItem> findByOrderId(Long orderId);
}
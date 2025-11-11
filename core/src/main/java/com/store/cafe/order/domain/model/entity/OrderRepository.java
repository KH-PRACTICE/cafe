package com.store.cafe.order.domain.model.entity;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findByOrderId(Long orderId);
}
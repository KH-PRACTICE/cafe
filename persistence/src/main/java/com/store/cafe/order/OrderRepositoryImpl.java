package com.store.cafe.order;

import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.model.entity.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public Optional<Order> findByOrderId(Long orderId) {
        return jpaRepository.findById(orderId);
    }
}
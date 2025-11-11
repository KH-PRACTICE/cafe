package com.store.cafe.order;

import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.model.entity.OrderItem;
import com.store.cafe.order.domain.model.entity.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository jpaRepository;

    @Override
    public OrderItem save(OrderItem orderItem) {
        return jpaRepository.save(orderItem);
    }

    @Override
    public void saveAll(List<OrderItem> orderItems) {
        jpaRepository.saveAll(orderItems);
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId);
    }
}
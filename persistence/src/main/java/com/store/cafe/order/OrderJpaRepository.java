package com.store.cafe.order;

import com.store.cafe.order.domain.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
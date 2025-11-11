package com.store.cafe.order.application.result;

import com.store.cafe.order.domain.enums.OrderStatus;
import com.store.cafe.order.domain.model.entity.Order;

import java.time.ZonedDateTime;

public record OrderResult(
        Long orderId,
        Long totalAmount,
        OrderStatus status,
        ZonedDateTime orderedAt
) {

    public static OrderResult from(Order order, boolean isSuccess) {
        return new OrderResult(
                order.getOrderId(),
                order.getTotalAmount(),
                isSuccess ? OrderStatus.COMPLETED : OrderStatus.FAILED,
                order.getOrderedAt()
        );
    }
}
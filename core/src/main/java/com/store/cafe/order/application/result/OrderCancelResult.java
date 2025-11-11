package com.store.cafe.order.application.result;

import com.store.cafe.order.domain.enums.OrderStatus;

import java.time.ZonedDateTime;

public record OrderCancelResult(
        Long orderId,
        boolean success,
        OrderStatus status,
        ZonedDateTime canceledAt
) {

    public static OrderCancelResult success(Long orderId, ZonedDateTime canceledAt) {
        return new OrderCancelResult(orderId, true, OrderStatus.CANCELED, canceledAt);
    }

    public static OrderCancelResult failure(Long orderId) {
        return new OrderCancelResult(orderId, false, null, null);
    }

}

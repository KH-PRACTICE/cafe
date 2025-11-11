package com.store.cafe.order.application.command;

public record OrderItemCommand(
        Long productId,
        Long quantity
) {
}
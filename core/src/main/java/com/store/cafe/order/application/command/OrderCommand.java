package com.store.cafe.order.application.command;

import java.util.List;

public record OrderCommand(
        Long memberUid,
        List<OrderItemCommand> items
) {
}
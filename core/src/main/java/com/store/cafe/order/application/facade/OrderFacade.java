package com.store.cafe.order.application.facade;

import com.store.cafe.order.application.command.OrderCommand;
import com.store.cafe.order.application.result.OrderCancelResult;
import com.store.cafe.order.application.result.OrderResult;

public interface OrderFacade {

    OrderResult order(OrderCommand command);

    OrderCancelResult cancelOrder(Long orderId, Long memberUid);
}
package com.store.cafe.order.application.facade;

import com.store.cafe.order.application.command.OrderCommand;
import com.store.cafe.order.application.result.OrderCancelResult;
import com.store.cafe.order.application.result.OrderResult;

public interface OrderFacade {

    /**
     * 주문 요청
     * @param command (memberUid, items)
     * @return OrderResult (orderId, totalAmount, status, orderedAt)
     */
    OrderResult order(OrderCommand command);


    /**
     * 주문 취소
     * @param orderId, memberUid
     * @return OrderCancelResult (orderId, success, status, canceledAt)
     */
    OrderCancelResult cancelOrder(Long orderId, Long memberUid);
}
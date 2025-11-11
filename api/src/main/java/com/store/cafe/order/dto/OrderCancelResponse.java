package com.store.cafe.order.dto;

import com.store.cafe.order.application.result.OrderCancelResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "주문 취소 결과 응답")
public record OrderCancelResponse(

        @Schema(description = "취소된 주문 번호")
        Long orderId,

        @Schema(description = "주문 상태")
        String status,

        @Schema(description = "주문 취소 시간")
        ZonedDateTime canceledAt
) {

    public static OrderCancelResponse from(OrderCancelResult result) {
        return new OrderCancelResponse(
                result.orderId(),
                result.status().getCode(),
                result.canceledAt()
        );
    }
}

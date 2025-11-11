package com.store.cafe.order.dto;

import com.store.cafe.order.application.result.OrderResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "주문 결과 응답")
public record OrderResponse(

        @Schema(description = "주문 번호")
        Long orderId,

        @Schema(description = "총 금액")
        Long totalAmount,

        @Schema(description = "주문 상태")
        String status,

        @Schema(description = "주문 시각")
        ZonedDateTime orderedAt

) {
    public static OrderResponse from(OrderResult result) {
        return new OrderResponse(
                result.orderId(),
                result.totalAmount(),
                result.status().getCode(),
                result.orderedAt()
        );
    }
}
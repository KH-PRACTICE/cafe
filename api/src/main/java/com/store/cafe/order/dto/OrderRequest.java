package com.store.cafe.order.dto;

import com.store.cafe.order.application.command.OrderCommand;
import com.store.cafe.order.application.command.OrderItemCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "주문 요청")
public record OrderRequest(

        @Schema(description = "주문 상품 목록")
        @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다")
        List<OrderItemRequest> items
) {

    public OrderCommand toCommand(Long memberUid) {
        return new OrderCommand(
                memberUid,
                items.stream()
                        .map(OrderItemRequest::toCommand)
                        .toList()
        );
    }

    @Schema(description = "주문 상품")
    public record OrderItemRequest(

            @Schema(description = "상품 ID", example = "1")
            @NotNull(message = "상품 ID는 필수입니다")
            Long productId,

            @Schema(description = "수량", example = "2")
            @NotNull(message = "수량은 필수입니다")
            @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
            Long quantity

    ) {
        public OrderItemCommand toCommand() {
            return new OrderItemCommand(productId, quantity);
        }
    }
}
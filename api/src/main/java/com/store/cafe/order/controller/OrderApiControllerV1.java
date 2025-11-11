package com.store.cafe.order.controller;

import com.store.cafe.common.dto.CommonResponseV1;
import com.store.cafe.order.application.facade.OrderFacade;
import com.store.cafe.order.application.result.OrderCancelResult;
import com.store.cafe.order.application.result.OrderResult;
import com.store.cafe.order.dto.OrderCancelResponse;
import com.store.cafe.order.dto.OrderRequest;
import com.store.cafe.order.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Tag(name = "주문", description = "주문 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderApiControllerV1 {

    private final OrderFacade orderFacade;

    @PostMapping("/orders")
    @Operation(summary = "상품 주문", description = "선택한 상품들을 주문합니다")
    public CommonResponseV1<OrderResponse> createOrder(
            @RequestHeader("X-Member-Uid") Long memberUid,
            @RequestBody @Valid OrderRequest request) {

        OrderResult result = orderFacade.order(request.toCommand(memberUid));
        return CommonResponseV1.success(OrderResponse.from(result));
    }

    @PostMapping("/orders/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "특정 주문을 취소합니다")
    public CommonResponseV1<OrderCancelResponse> cancelOrder(
            @RequestHeader("X-Member-Uid") Long memberUid,
            @PathVariable(value = "orderId") Long orderId) {

        OrderCancelResult orderCancelResult = orderFacade.cancelOrder(orderId, memberUid);
        return CommonResponseV1.success(OrderCancelResponse.from(orderCancelResult));
    }
}
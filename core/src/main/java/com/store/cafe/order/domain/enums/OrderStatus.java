package com.store.cafe.order.domain.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("PENDING", "주문 보류 상태"),
    CONFIRMED("CONFIRMED", "주문 확정 상태"),
    COMPLETED("COMPLETED", "주문 완료 상태"),
    CANCELED("CANCELED", "주문 취소 상태"),
    FAILED("FAILED", "주문 실패 상태"),
    DELIVERED("DELIVERED ", "주문 완료 및 배송중 상태");

    private final String code;
    private final String description;

    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public boolean canCancel() {
        return this == PENDING || this == COMPLETED;
    }
}

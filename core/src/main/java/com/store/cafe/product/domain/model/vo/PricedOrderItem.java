package com.store.cafe.product.domain.model.vo;

public record PricedOrderItem(
        Long productId,
        Long quantity,
        Long price
) {

    public Long totalPrice() {
        return quantity * price;
    }

    public static PricedOrderItem of(Long productId, Long quantity, Long price) {
        return new PricedOrderItem(productId, quantity, price);
    }
}

package com.store.cafe.product.domain.exception;

public class OutOfProductStockException extends RuntimeException {
    public OutOfProductStockException(String message) {
        super(message);
    }
}

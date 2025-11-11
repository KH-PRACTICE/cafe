package com.store.cafe.order.domain.exception;

public class OrderCancelUnableException extends RuntimeException {
    public OrderCancelUnableException(String message) {
        super(message);
    }
}

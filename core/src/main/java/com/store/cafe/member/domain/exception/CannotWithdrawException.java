package com.store.cafe.member.domain.exception;

public class CannotWithdrawException extends RuntimeException {
    public CannotWithdrawException(String message) {
        super(message);
    }
}

package com.store.cafe.member.domain.exception;

public class CannotWithdrawCancelException extends RuntimeException {
  public CannotWithdrawCancelException(String message) {
    super(message);
  }
}

package com.store.cafe.common.exception;

import com.store.cafe.common.dto.CommonResponseV1;
import com.store.cafe.member.domain.exception.CannotWithdrawCancelException;
import com.store.cafe.member.domain.exception.CannotWithdrawException;
import com.store.cafe.member.domain.exception.DuplicateLoginIdException;
import com.store.cafe.member.domain.exception.MemberNotFoundException;
import com.store.cafe.order.domain.exception.OrderCancelUnableException;
import com.store.cafe.order.domain.exception.OrderItemNotFoundException;
import com.store.cafe.order.domain.exception.OrderNotFoundException;
import com.store.cafe.payment.domain.exception.PaymentHistoryNotFoundException;
import com.store.cafe.product.domain.exception.OutOfProductStockException;
import com.store.cafe.product.domain.exception.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<CommonResponseV1<Void>> handleDuplicateLoginIdException(DuplicateLoginIdException e) {
        log.warn("DuplicateLoginIdException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CommonResponseV1.error(e.getMessage()));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<CommonResponseV1<Void>> handleMemberNotFoundException(MemberNotFoundException e) {
        log.warn("MemberNotFoundException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponseV1.error(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonResponseV1<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("AuthenticationException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponseV1.error(e.getMessage()));
    }

    @ExceptionHandler(CannotWithdrawException.class)
    public ResponseEntity<CommonResponseV1<Void>> handleCannotWithdrawException(CannotWithdrawException e) {
        log.warn("CannotWithdrawException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseV1.error(e.getMessage()));
    }

    @ExceptionHandler(CannotWithdrawCancelException.class)
    public ResponseEntity<CommonResponseV1<Void>> handleCannotWithdrawCancelException(CannotWithdrawCancelException e) {
        log.warn("CannotWithdrawCancelException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseV1.error(e.getMessage()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<CommonResponseV1<Void>> handleOrderNotFoundException(OrderNotFoundException e) {
        log.warn("OrderNotFoundException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponseV1.error(e.getMessage()));
    }

    @ExceptionHandler(OrderItemNotFoundException.class)
    public ResponseEntity<CommonResponseV1<Void>> handleOrderItemNotFoundException(OrderItemNotFoundException e) {
        log.warn("OrderItemNotFoundException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponseV1.error(e.getMessage()));
    }

    @ExceptionHandler(OrderCancelUnableException.class)
    public ResponseEntity<CommonResponseV1<Void>> handleOrderCancelUnableException(OrderCancelUnableException e) {
        log.warn("OrderCancelUnableException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseV1.error(e.getMessage()));
    }

    @ExceptionHandler(PaymentHistoryNotFoundException.class)
    public ResponseEntity<CommonResponseV1<Void>> handlePaymentHistoryNotFoundException(PaymentHistoryNotFoundException e) {
        log.warn("PaymentHistoryNotFoundException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponseV1.error(e.getMessage()));
    }

    @ExceptionHandler(OutOfProductStockException.class)
    public ResponseEntity<CommonResponseV1<Void>> handleOutOfProductStockException(OutOfProductStockException e) {
        log.warn("OutOfProductStockException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseV1.error(e.getMessage()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<CommonResponseV1<Void>> handleProductNotFoundException(ProductNotFoundException e) {
        log.warn("ProductNotFoundException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponseV1.error(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseV1<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.warn("Validation error occurred: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseV1.error("필수 파라미터 검증 실패 : " + errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseV1<Void>> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponseV1.error("내부적인 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."));
    }
}
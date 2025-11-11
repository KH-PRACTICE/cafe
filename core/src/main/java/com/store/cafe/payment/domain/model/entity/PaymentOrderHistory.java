package com.store.cafe.payment.domain.model.entity;

import com.store.cafe.payment.domain.enums.PaymentStatus;
import com.store.cafe.util.DateUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "payment_order_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class PaymentOrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    private PaymentOrderHistory(Long orderId, String transactionId, PaymentStatus status) {

        ZonedDateTime now = DateUtil.now();

        this.orderId = orderId;
        this.transactionId = transactionId;
        this.status = status;
        this.updatedAt = now;
        this.createdAt = now;
    }

    public static PaymentOrderHistory of(Long orderId, String transactionId, PaymentStatus status) {
        return new PaymentOrderHistory(orderId, transactionId, status);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = DateUtil.now();
    }

}
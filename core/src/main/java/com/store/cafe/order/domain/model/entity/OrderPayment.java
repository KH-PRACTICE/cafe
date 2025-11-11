package com.store.cafe.order.domain.model.entity;

import com.store.cafe.util.DateUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "requested_at", nullable = false)
    private ZonedDateTime requestedAt;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;

    @Column(name = "cancelled_at")
    private ZonedDateTime cancelledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    private PaymentOrder(Long orderId, String transactionId, Long amount, String status, ZonedDateTime requestedAt) {
        ZonedDateTime now = DateUtil.now();
        
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.status = status;
        this.requestedAt = requestedAt;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static PaymentOrder of(Long orderId, String transactionId, Long amount, String status, ZonedDateTime requestedAt) {
        return new PaymentOrder(orderId, transactionId, amount, status, requestedAt);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = DateUtil.now();
    }
}
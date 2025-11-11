package com.store.cafe.order.domain.model.entity;

import com.store.cafe.order.domain.enums.OrderStatus;
import com.store.cafe.order.domain.exception.OrderCancelUnableException;
import com.store.cafe.product.domain.model.vo.PricedOrderItem;
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
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "member_uid", nullable = false)
    private Long memberUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "ordered_at", nullable = false, updatable = false)
    private ZonedDateTime orderedAt;

    @Column(name = "canceled_at", updatable = false)
    private ZonedDateTime canceledAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    private Order(Long memberUid, OrderStatus status, Long totalAmount) {
        ZonedDateTime now = DateUtil.now();

        this.memberUid = memberUid;
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderedAt = now;
        this.updatedAt = now;
    }

    public static Order create(Long memberUid, List<PricedOrderItem> pricedItems) {

        Long totalAmount = pricedItems.stream()
                .mapToLong(PricedOrderItem::totalPrice)
                .sum();

        return new Order(memberUid, OrderStatus.PENDING, totalAmount);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = DateUtil.now();
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
        this.canceledAt = DateUtil.now();
    }

    public void fail() {
        this.status = OrderStatus.FAILED;
    }

    public void validateCancellable(Long memberUid) {

        if (!this.memberUid.equals(memberUid)) {
            throw new OrderCancelUnableException("본인 주문만 취소 가능합니다. : " + this.memberUid + ", 요청자: " + memberUid);
        }

        if (!status.canCancel()) {
            throw new OrderCancelUnableException("주문 취소가 불가능한 상태입니다. 현재 상태: " + status);
        }
    }
}
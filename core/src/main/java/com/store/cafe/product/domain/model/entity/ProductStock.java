package com.store.cafe.product.domain.model.entity;

import com.store.cafe.product.domain.exception.OutOfProductStockException;
import com.store.cafe.util.DateUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "product_stock")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductStock {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    private ProductStock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.updatedAt = DateUtil.now();
    }

    public static ProductStock of(Long productId, Long quantity) {
        return new ProductStock(productId, quantity);
    }

    public void decrease(Long orderQuantity) {
        if (this.quantity < orderQuantity) {
            throw new OutOfProductStockException("stock is insufficient for productId: " + this.productId);
        }
        this.quantity -= orderQuantity;
        this.updatedAt = DateUtil.now();
    }

    public void increase(Long quantity) {
        this.quantity += quantity;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = DateUtil.now();
    }

}
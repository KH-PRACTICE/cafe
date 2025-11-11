package com.store.cafe.product;

import com.store.cafe.product.domain.model.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;

public interface ProductStockJpaRepository extends JpaRepository<ProductStock, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ps from ProductStock ps where ps.productId in :productIds")
    List<ProductStock> findAllByProductIdInForUpdate(@Param("productIds") List<Long> productIds);
}
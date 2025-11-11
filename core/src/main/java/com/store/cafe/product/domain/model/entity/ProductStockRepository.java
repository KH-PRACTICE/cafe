package com.store.cafe.product.domain.model.entity;

import java.util.List;
import java.util.Optional;

public interface ProductStockRepository {

    ProductStock save(ProductStock productStock);

    Optional<ProductStock> findByProductId(Long productId);

    List<ProductStock> findAllByProductIdInForUpdate(List<Long> productIds);
}
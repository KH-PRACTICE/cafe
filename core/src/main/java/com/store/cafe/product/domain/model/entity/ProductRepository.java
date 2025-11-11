package com.store.cafe.product.domain.model.entity;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findByProductId(Long productId);
    
    List<Product> findByProductIds(List<Long> productIds);
}
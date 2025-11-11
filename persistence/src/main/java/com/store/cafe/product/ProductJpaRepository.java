package com.store.cafe.product;

import com.store.cafe.product.domain.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}
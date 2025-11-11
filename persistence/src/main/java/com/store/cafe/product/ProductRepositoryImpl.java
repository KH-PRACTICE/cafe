package com.store.cafe.product;

import com.store.cafe.product.domain.model.entity.Product;
import com.store.cafe.product.domain.model.entity.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    @Override
    public Product save(Product product) {
        return jpaRepository.save(product);
    }

    @Override
    public Optional<Product> findByProductId(Long productId) {
        return jpaRepository.findById(productId);
    }
    
    @Override
    public List<Product> findByProductIds(List<Long> productIds) {
        return jpaRepository.findAllById(productIds);
    }
}
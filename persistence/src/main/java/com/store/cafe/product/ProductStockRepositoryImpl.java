package com.store.cafe.product;

import com.store.cafe.product.domain.model.entity.ProductStock;
import com.store.cafe.product.domain.model.entity.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductStockRepositoryImpl implements ProductStockRepository {

    private final ProductStockJpaRepository jpaRepository;

    @Override
    public ProductStock save(ProductStock productStock) {
        return jpaRepository.save(productStock);
    }

    @Override
    public Optional<ProductStock> findByProductId(Long productId) {
        return jpaRepository.findById(productId);
    }

    @Override
    public List<ProductStock> findAllByProductIdInForUpdate(List<Long> productIds) {
        return jpaRepository.findAllByProductIdInForUpdate(productIds);
    }
}
package com.store.cafe.product;

import com.store.cafe.product.domain.model.entity.ProductStock;
import com.store.cafe.product.domain.model.entity.ProductStockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ProductStockRepositoryImpl.class})
class ProductStockRepositoryImplTest {

    @Autowired
    private ProductStockRepository productStockRepository;

    @Test
    @DisplayName("ProductStockRepositoryImpl.findByProductId() - 존재하지 않는 ProductId로 조회하면 빈 Optional을 반환한다")
    void findByProductId_notFound() {
        // when
        Optional<ProductStock> result = productStockRepository.findByProductId(999L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("ProductStockRepositoryImpl.findAllByProductIdInForUpdate() - 여러 상품의 재고를 조회할 수 있다")
    void findAllByProductIdInForUpdate_success() {
        // given
        ProductStock stock1 = ProductStock.of(10L, 100L);
        ProductStock stock2 = ProductStock.of(20L, 200L);
        ProductStock stock3 = ProductStock.of(30L, 300L);
        
        productStockRepository.save(stock1);
        productStockRepository.save(stock2);
        productStockRepository.save(stock3);

        // when
        List<Long> productIds = List.of(10L, 20L, 30L);
        List<ProductStock> stocks = productStockRepository.findAllByProductIdInForUpdate(productIds);

        // then
        assertThat(stocks).hasSize(3);
        assertThat(stocks).extracting("productId").containsExactlyInAnyOrder(10L, 20L, 30L);
        assertThat(stocks).extracting("quantity").containsExactlyInAnyOrder(100L, 200L, 300L);
    }
}
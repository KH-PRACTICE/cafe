package com.store.cafe.product.domain.service;

import com.store.cafe.product.domain.exception.ProductNotFoundException;
import com.store.cafe.product.domain.model.entity.Product;
import com.store.cafe.product.domain.model.entity.ProductRepository;
import com.store.cafe.product.domain.model.vo.PricedOrderItem;
import com.store.cafe.product.domain.model.vo.StockDecreaseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProductReadServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductReadService productReadService;

    @Test
    @DisplayName("재고 차감 결과에 상품 가격을 붙여서 반환한다")
    void attachPrices_Success() {
        // given
        Long productId1 = 1L;
        Long productId2 = 2L;
        Long quantity1 = 2L;
        Long quantity2 = 3L;
        Long price1 = 4500L;
        Long price2 = 5000L;

        List<StockDecreaseResult> stockResults = List.of(
                new StockDecreaseResult(productId1, quantity1),
                new StockDecreaseResult(productId2, quantity2)
        );

        Product product1 = Product.of("아메리카노", price1, "진한 커피");
        Product product2 = Product.of("카페라떼", price2, "부드러운 커피");

        setProductId(product1, productId1);
        setProductId(product2, productId2);

        given(productRepository.findByProductIds(anyList()))
                .willReturn(List.of(product1, product2));

        // when
        List<PricedOrderItem> result = productReadService.attachPrices(stockResults);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).productId()).isEqualTo(productId1);
        assertThat(result.get(0).quantity()).isEqualTo(quantity1);
        assertThat(result.get(0).price()).isEqualTo(price1);
        assertThat(result.get(1).productId()).isEqualTo(productId2);
        assertThat(result.get(1).quantity()).isEqualTo(quantity2);
        assertThat(result.get(1).price()).isEqualTo(price2);

        then(productRepository).should(times(1)).findByProductIds(anyList());
    }

    @Test
    @DisplayName("상품을 찾을 수 없으면 예외가 발생한다")
    void attachPrices_ProductNotFound_ThrowsException() {
        // given
        Long productId = 999L;
        Long quantity = 1L;

        List<StockDecreaseResult> stockResults = List.of(
                new StockDecreaseResult(productId, quantity)
        );

        given(productRepository.findByProductIds(anyList()))
                .willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> productReadService.attachPrices(stockResults))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Not Found Product. productId=" + productId);

        then(productRepository).should(times(1)).findByProductIds(anyList());
    }

    @Test
    @DisplayName("빈 재고 차감 결과에 대해 빈 리스트를 반환한다")
    void attachPrices_EmptyList_ReturnsEmptyList() {
        // given
        List<StockDecreaseResult> emptyStockResults = List.of();

        given(productRepository.findByProductIds(anyList()))
                .willReturn(List.of());

        // when
        List<PricedOrderItem> result = productReadService.attachPrices(emptyStockResults);

        // then
        assertThat(result).isEmpty();
        then(productRepository).should(times(1)).findByProductIds(anyList());
    }

    private void setProductId(Product product, Long productId) {
        try {
            var field = Product.class.getDeclaredField("productId");
            field.setAccessible(true);
            field.set(product, productId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set productId", e);
        }
    }
}
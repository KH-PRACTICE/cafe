package com.store.cafe.product.domain.service;

import com.store.cafe.order.application.command.OrderItemCommand;
import com.store.cafe.order.domain.model.entity.OrderItem;
import com.store.cafe.product.domain.exception.ProductNotFoundException;
import com.store.cafe.product.domain.model.entity.ProductStock;
import com.store.cafe.product.domain.model.entity.ProductStockRepository;
import com.store.cafe.product.domain.model.vo.StockDecreaseResult;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductStockServiceTest {

    @Mock
    private ProductStockRepository productStockRepository;

    @InjectMocks
    private ProductStockService productStockService;

    private List<OrderItemCommand> testCommands;
    private List<OrderItem> testOrderItems;
    private List<ProductStock> testProductStocks;

    @BeforeEach
    void setUp() {
        testCommands = List.of(
                new OrderItemCommand(1L, 2L),
                new OrderItemCommand(2L, 3L)
        );

        testOrderItems = List.of(
                OrderItem.of(1L, 1L, 2L, 10000L),
                OrderItem.of(1L, 2L, 3L, 15000L)
        );
    }

    @Test
    @DisplayName("재고 감소가 성공적으로 처리되어야 한다")
    void decreaseStocks_Success() {

        testProductStocks = List.of(
                mock(ProductStock.class),
                mock(ProductStock.class)
        );

        when(testProductStocks.get(0).getProductId()).thenReturn(1L);
        when(testProductStocks.get(1).getProductId()).thenReturn(2L);

        // given
        when(productStockRepository.findAllByProductIdInForUpdate(anyList())).thenReturn(testProductStocks);

        // when
        List<StockDecreaseResult> results = productStockService.decreaseStocks(testCommands);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).productId()).isEqualTo(1L);
        assertThat(results.get(0).quantity()).isEqualTo(2L);
        assertThat(results.get(1).productId()).isEqualTo(2L);
        assertThat(results.get(1).quantity()).isEqualTo(3L);

        verify(testProductStocks.get(0)).decrease(2L);
        verify(testProductStocks.get(1)).decrease(3L);
        verify(productStockRepository).findAllByProductIdInForUpdate(anyList());
    }

    @Test
    @DisplayName("존재하지 않는 상품의 재고 감소 시 예외가 발생해야 한다")
    void decreaseStocks_ProductNotFound_ThrowsException() {
        // given
        List<ProductStock> emptyStocks = List.of();
        when(productStockRepository.findAllByProductIdInForUpdate(anyList())).thenReturn(emptyStocks);

        // when & then
        assertThatThrownBy(() -> productStockService.decreaseStocks(testCommands))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("재고 정보 없음: 1");
    }

    @Test
    @DisplayName("재고 복원이 성공적으로 처리되어야 한다")
    void restoreStocks_Success() {

        testProductStocks = List.of(
                mock(ProductStock.class),
                mock(ProductStock.class)
        );

        when(testProductStocks.get(0).getProductId()).thenReturn(1L);
        when(testProductStocks.get(1).getProductId()).thenReturn(2L);

        // given
        when(productStockRepository.findAllByProductIdInForUpdate(anyList())).thenReturn(testProductStocks);

        // when
        productStockService.restoreStocks(testOrderItems);

        // then
        verify(testProductStocks.get(0)).increase(2L);
        verify(testProductStocks.get(1)).increase(3L);
        verify(productStockRepository).findAllByProductIdInForUpdate(anyList());
    }
}
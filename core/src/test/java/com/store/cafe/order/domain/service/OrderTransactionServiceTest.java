package com.store.cafe.order.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.store.cafe.order.application.command.OrderItemCommand;
import com.store.cafe.order.domain.enums.OrderStatus;
import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.model.entity.OrderItem;
import com.store.cafe.product.domain.model.vo.PricedOrderItem;
import com.store.cafe.product.domain.model.vo.StockDecreaseResult;
import com.store.cafe.product.domain.service.ProductReadService;
import com.store.cafe.product.domain.service.ProductStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderTransactionServiceTest {

    @Mock
    private OrderCreateService orderCreateService;

    @Mock
    private OrderReadService orderReadService;

    @Mock
    private ProductStockService productStockService;

    @Mock
    private ProductReadService productReadService;

    @InjectMocks
    private OrderTransactionService orderTransactionService;

    private Long testMemberUid;
    private Long testOrderId;
    private String testTransactionId;
    private List<OrderItemCommand> testOrderItems;

    @BeforeEach
    void setUp() {
        testMemberUid = 100L;
        testOrderId = 1L;
        testTransactionId = "transaction123";
        testOrderItems = List.of(
            new OrderItemCommand(1L, 2L),
            new OrderItemCommand(2L, 3L)
        );
    }

    @Test
    @DisplayName("재고 감소와 함께 주문 생성 - 성공")
    void createOrderWithStockDecrease_success() {
        List<StockDecreaseResult> mockStockResults = List.of(
            new StockDecreaseResult(1L, 2L),
            new StockDecreaseResult(2L, 3L)
        );
        List<PricedOrderItem> mockPricedItems = List.of(
            new PricedOrderItem(1L, 2L, 10000L),
            new PricedOrderItem(2L, 3L, 15000L)
        );
        Order mockOrder = Order.create(testMemberUid, mockPricedItems);

        when(productStockService.decreaseStocks(testOrderItems)).thenReturn(mockStockResults);
        when(productReadService.attachPrices(mockStockResults)).thenReturn(mockPricedItems);
        when(orderCreateService.createOrder(testMemberUid, mockPricedItems)).thenReturn(mockOrder);

        Order result = orderTransactionService.createOrderWithStockDecrease(testMemberUid, testOrderItems);

        assertThat(result).isNotNull();
        assertThat(result.getMemberUid()).isEqualTo(testMemberUid);
        verify(productStockService).decreaseStocks(testOrderItems);
        verify(productReadService).attachPrices(mockStockResults);
        verify(orderCreateService).createOrder(testMemberUid, mockPricedItems);
    }

    @Test
    @DisplayName("주문 완료 처리 - 성공")
    void completeOrder_success() {
        Order mockOrder = Order.create(testMemberUid, List.of());
        when(orderReadService.getOrder(testOrderId)).thenReturn(mockOrder);

        Order result = orderTransactionService.completeOrder(testOrderId);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        verify(orderReadService).getOrder(testOrderId);
    }

    @Test
    @DisplayName("주문 실패 처리 - 성공")
    void failOrder_success() {
        List<OrderItem> mockOrderItems = List.of(
            OrderItem.of(testOrderId, 1L, 2L, 10000L)
        );
        Order mockOrder = Order.create(testMemberUid, List.of());

        when(orderReadService.getOrderItems(testOrderId)).thenReturn(mockOrderItems);
        when(orderReadService.getOrder(testOrderId)).thenReturn(mockOrder);

        Order result = orderTransactionService.failOrder(testOrderId);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.FAILED);
        verify(productStockService).restoreStocks(mockOrderItems);
        verify(orderReadService).getOrderItems(testOrderId);
        verify(orderReadService).getOrder(testOrderId);
    }
}
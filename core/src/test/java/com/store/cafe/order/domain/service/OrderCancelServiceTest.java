package com.store.cafe.order.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.store.cafe.order.domain.enums.OrderStatus;
import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.model.entity.OrderItem;
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
class OrderCancelServiceTest {

    @Mock
    private OrderReadService orderReadService;

    @Mock
    private ProductStockService productStockService;

    @InjectMocks
    private OrderCancelService orderCancelService;

    private Long testOrderId;
    private Long testMemberUid;

    @BeforeEach
    void setUp() {
        testOrderId = 1L;
        testMemberUid = 100L;
    }

    @Test
    @DisplayName("주문 취소 시 재고 복원 및 주문 상태 변경")
    void cancelOrder_success() {
        List<OrderItem> mockOrderItems = List.of(
            OrderItem.of(testOrderId, 1L, 2L, 10000L)
        );
        Order mockOrder = Order.create(testMemberUid, List.of());

        when(orderReadService.getOrderItems(testOrderId)).thenReturn(mockOrderItems);
        when(orderReadService.getOrder(testOrderId)).thenReturn(mockOrder);

        Order result = orderCancelService.cancelOrder(testOrderId);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(productStockService).restoreStocks(mockOrderItems);
    }

    @Test
    @DisplayName("주문 취소 가능 여부 검증 - 성공")
    void validateCancellable_success() {
        Order mockOrder = Order.create(testMemberUid, List.of());
        when(orderReadService.getOrder(testOrderId)).thenReturn(mockOrder);

        orderCancelService.validateCancellable(testOrderId, testMemberUid);

        verify(orderReadService).getOrder(testOrderId);
    }

}
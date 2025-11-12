package com.store.cafe.order.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.store.cafe.order.domain.exception.OrderItemNotFoundException;
import com.store.cafe.order.domain.exception.OrderNotFoundException;
import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.model.entity.OrderItem;
import com.store.cafe.order.domain.model.entity.OrderItemRepository;
import com.store.cafe.order.domain.model.entity.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderReadServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderReadService orderReadService;

    private Long testOrderId;
    private Long testMemberUid;

    @BeforeEach
    void setUp() {
        testOrderId = 1L;
        testMemberUid = 100L;
    }

    @Test
    @DisplayName("주문 조회 - 성공")
    void getOrder_success() {
        Order mockOrder = Order.create(testMemberUid, List.of());
        when(orderRepository.findByOrderId(testOrderId)).thenReturn(Optional.of(mockOrder));

        Order result = orderReadService.getOrder(testOrderId);

        assertThat(result).isNotNull();
        assertThat(result.getMemberUid()).isEqualTo(testMemberUid);
    }

    @Test
    @DisplayName("주문 조회 - 존재하지 않는 주문")
    void getOrder_notFound() {
        when(orderRepository.findByOrderId(testOrderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderReadService.getOrder(testOrderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("존재 하지 않는 주문 입니다: " + testOrderId);
    }

    @Test
    @DisplayName("주문 아이템 조회 - 성공")
    void getOrderItems_success() {
        List<OrderItem> mockOrderItems = List.of(
            OrderItem.of(testOrderId, 1L, 2L, 10000L),
            OrderItem.of(testOrderId, 2L, 1L, 15000L)
        );
        when(orderItemRepository.findByOrderId(testOrderId)).thenReturn(mockOrderItems);

        List<OrderItem> result = orderReadService.getOrderItems(testOrderId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getOrderId()).isEqualTo(testOrderId);
        assertThat(result.get(1).getOrderId()).isEqualTo(testOrderId);
    }

    @Test
    @DisplayName("주문 아이템 조회 - 빈 리스트")
    void getOrderItems_empty() {
        when(orderItemRepository.findByOrderId(testOrderId)).thenReturn(List.of());

        assertThatThrownBy(() -> orderReadService.getOrderItems(testOrderId))
                .isInstanceOf(OrderItemNotFoundException.class)
                .hasMessage("주문 상품 정보가 없습니다. orderId=" + testOrderId);
    }
}
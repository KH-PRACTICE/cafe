package com.store.cafe.order.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.store.cafe.order.domain.model.entity.Order;
import com.store.cafe.order.domain.model.entity.OrderItemRepository;
import com.store.cafe.order.domain.model.entity.OrderRepository;
import com.store.cafe.product.domain.model.vo.PricedOrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderCreateServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderCreateService orderCreateService;

    private Long testMemberUid;
    private List<PricedOrderItem> testPricedItems;

    @BeforeEach
    void setUp() {
        testMemberUid = 100L;
        testPricedItems = List.of(
            new PricedOrderItem(1L, 2L, 10000L),
            new PricedOrderItem(2L, 3L, 15000L)
        );
    }

    @Test
    @DisplayName("주문 생성 시 주문과 주문 아이템이 모두 저장됨")
    void createOrder_success() {
        Order mockOrder = Order.create(testMemberUid, testPricedItems);

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        Order result = orderCreateService.createOrder(testMemberUid, testPricedItems);

        assertThat(result).isNotNull();
        assertThat(result.getMemberUid()).isEqualTo(testMemberUid);
        
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("빈 주문 아이템 리스트로 주문 생성")
    void createOrder_emptyItems() {
        List<PricedOrderItem> emptyItems = List.of();
        Order mockOrder = Order.create(testMemberUid, emptyItems);

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        Order result = orderCreateService.createOrder(testMemberUid, emptyItems);

        assertThat(result).isNotNull();
        assertThat(result.getMemberUid()).isEqualTo(testMemberUid);
        
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(anyList());
    }
}
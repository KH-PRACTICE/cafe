package com.store.cafe.order;

import com.store.cafe.order.domain.model.entity.OrderItem;
import com.store.cafe.order.domain.model.entity.OrderItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({OrderItemRepositoryImpl.class})
class OrderItemRepositoryImplTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("OrderItemRepositoryImpl.save() - 주문 아이템을 저장할 수 있다")
    void save_success() {
        // given
        OrderItem orderItem = OrderItem.of(100L, 1L, 2L, 5000L);

        // when
        OrderItem saved = orderItemRepository.save(orderItem);
        entityManager.flush();

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getOrderItemId()).isNotNull();
        assertThat(saved.getOrderId()).isEqualTo(100L);
        assertThat(saved.getProductId()).isEqualTo(1L);
        assertThat(saved.getQuantity()).isEqualTo(2L);
        assertThat(saved.getPrice()).isEqualTo(5000L);
        assertThat(saved.getCreatedAt()).isNotNull();

        OrderItem found = entityManager.find(OrderItem.class, saved.getOrderItemId());
        assertThat(found).isNotNull();
        assertThat(found.getOrderId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("OrderItemRepositoryImpl.saveAll() - 여러 주문 아이템을 한 번에 저장할 수 있다")
    void saveAll_success() {

        // given
        OrderItem item1 = OrderItem.of(200L, 1L, 1L, 4500L);
        OrderItem item2 = OrderItem.of(200L, 2L, 2L, 5000L);
        OrderItem item3 = OrderItem.of(200L, 3L, 1L, 5500L);
        List<OrderItem> orderItems = List.of(item1, item2, item3);

        // when
        orderItemRepository.saveAll(orderItems);
        entityManager.flush();

        // then
        assertThat(item1.getOrderItemId()).isNotNull();
        assertThat(item2.getOrderItemId()).isNotNull();
        assertThat(item3.getOrderItemId()).isNotNull();

        OrderItem found1 = entityManager.find(OrderItem.class, item1.getOrderItemId());
        OrderItem found2 = entityManager.find(OrderItem.class, item2.getOrderItemId());
        OrderItem found3 = entityManager.find(OrderItem.class, item3.getOrderItemId());

        assertThat(found1).isNotNull();
        assertThat(found1.getOrderId()).isEqualTo(200L);
        assertThat(found1.getProductId()).isEqualTo(1L);

        assertThat(found2).isNotNull();
        assertThat(found2.getOrderId()).isEqualTo(200L);
        assertThat(found2.getProductId()).isEqualTo(2L);

        assertThat(found3).isNotNull();
        assertThat(found3.getOrderId()).isEqualTo(200L);
        assertThat(found3.getProductId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("OrderItemRepositoryImpl.findByOrderId() - orderID 로 OrderItem 들을 조회할 수 있다")
    void findByOrderId_success() {
        // given
        Long orderId = 300L;
        OrderItem item1 = OrderItem.of(orderId, 1L, 2L, 4500L);
        OrderItem item2 = OrderItem.of(orderId, 2L, 1L, 5000L);
        OrderItem item3 = OrderItem.of(orderId, 3L, 3L, 5500L);
        
        OrderItem otherOrderItem = OrderItem.of(999L, 4L, 1L, 6000L);

        orderItemRepository.save(item1);
        orderItemRepository.save(item2);
        orderItemRepository.save(item3);
        orderItemRepository.save(otherOrderItem);
        entityManager.flush();
        entityManager.clear();

        // when
        List<OrderItem> foundItems = orderItemRepository.findByOrderId(orderId);

        // then
        assertThat(foundItems).hasSize(3);
        assertThat(foundItems).allMatch(item -> item.getOrderId().equals(orderId));
        assertThat(foundItems).extracting("productId").containsExactlyInAnyOrder(1L, 2L, 3L);
        assertThat(foundItems).extracting("quantity").containsExactlyInAnyOrder(2L, 1L, 3L);
        assertThat(foundItems).extracting("price").containsExactlyInAnyOrder(4500L, 5000L, 5500L);
    }

    @Test
    @DisplayName("OrderItemRepositoryImpl.findByOrderId() - 존재하지 않는 주문 ID로 조회하면 빈 리스트를 반환한다")
    void findByOrderId_notFound() {
        // when
        List<OrderItem> result = orderItemRepository.findByOrderId(99999L);

        // then
        assertThat(result).isEmpty();
    }
}
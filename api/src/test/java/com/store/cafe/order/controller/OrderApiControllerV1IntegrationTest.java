package com.store.cafe.order.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.cafe.member.dto.MemberSignupRequest;
import com.store.cafe.order.dto.OrderRequest;
import com.store.cafe.payment.gateway.TestPaymentGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderApiControllerV1IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestPaymentGateway testPaymentGateway;

    @Test
    @DisplayName("주문이 성공적으로 처리된다.")
    void createOrder_ForcePaymentSuccess() throws Exception {
        try {

            // given - 결제 성공 강제 설정
            testPaymentGateway.forceSuccess();

            Long memberUid = createTestMember("paymentUser1");
            OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest(1L, 1L);
            OrderRequest orderRequest = new OrderRequest(List.of(item));

            // when & then
            mockMvc.perform(post("/api/v1/order/orders")
                            .header("X-Member-Uid", memberUid)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.resultMessage").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.orderId").exists())
                    .andExpect(jsonPath("$.data.status").value("COMPLETED"));
        } finally {
            testPaymentGateway.reset();
        }
    }

    @Test
    @DisplayName("주문을 했지만 결제 실패로 인해 주문이 실패한다.")
    void createOrder_ForcePaymentFailure() throws Exception {
        try {
            // given - 결제 실패 강제 설정
            testPaymentGateway.forceFailure();

            Long memberUid = createTestMember("paymentUser2");
            OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest(1L, 1L);
            OrderRequest orderRequest = new OrderRequest(List.of(item));

            // when & then
            mockMvc.perform(post("/api/v1/order/orders")
                            .header("X-Member-Uid", memberUid)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.resultMessage").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.orderId").exists())
                    .andExpect(jsonPath("$.data.status").value("FAILED"));
        } finally {
            // 테스트 후 MockPaymentGateway 설정 초기화
            testPaymentGateway.reset();
        }
    }

    @Test
    @DisplayName("주문 후 주문 취소가 성공적으로 처리된다")
    void cancelOrder_RealIntegration_Success() throws Exception {

        // given - 결제 성공 강제 설정
        testPaymentGateway.forceSuccess();

        // 1. 회원 가입
        Long memberUid = createTestMember("testUser2");

        // 2. 주문 생성
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest(1L, 1L);
        OrderRequest orderRequest = new OrderRequest(List.of(item));

        String orderResponse = mockMvc.perform(post("/api/v1/order/orders")
                        .header("X-Member-Uid", memberUid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(orderResponse);
        Long orderId = jsonNode.path("data").path("orderId").asLong();

        // when & then - 주문 취소
        mockMvc.perform(post("/api/v1/order/orders/{orderId}/cancel", orderId)
                        .header("X-Member-Uid", memberUid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resultMessage").value("SUCCESS"))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.canceledAt").exists());
    }

    @Test
    @DisplayName("재고 부족 시 주문이 실패한다")
    void createOrder_OutOfStock_Failure() throws Exception {

        // given
        Long memberUid = createTestMember("testUser3");

        //상품번호 3L : 재고 1개
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest(3L, 5L);
        OrderRequest orderRequest = new OrderRequest(List.of(item));

        // when & then
        mockMvc.perform(post("/api/v1/order/orders")
                        .header("X-Member-Uid", memberUid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("동시에 같은 상품 주문 시 Lock 이 적용되어 한 건만 성공한다")
    void createOrder_ConcurrentOrder_PessimisticLock() throws Exception {

        // 상품번호 3L : 재고가 1개인 상황에서 동시에 1개씩 주문
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest(3L, 1L);
        OrderRequest orderRequest = new OrderRequest(List.of(item));

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // when - 동시 주문 실행
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                Long memberUid1 = createTestMember("testUser1");

                return mockMvc.perform(post("/api/v1/order/orders")
                                .header("X-Member-Uid", memberUid1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderRequest)))
                        .andReturn().getResponse().getStatus();
            } catch (Exception e) {
                return 500;
            }
        }, executor);

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                Long memberUid2 = createTestMember("testUser12");

                return mockMvc.perform(post("/api/v1/order/orders")
                                .header("X-Member-Uid", memberUid2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderRequest)))
                        .andReturn().getResponse().getStatus();
            } catch (Exception e) {
                return 500;
            }
        }, executor);

        // then - 하나는 성공(200), 하나는 실패(4xx)해야 함
        Integer status1 = future1.get();
        Integer status2 = future2.get();

        boolean oneSuccessOneFailure = (status1 == 200 && status2 >= 400) || (status1 >= 400 && status2 == 200);
        assert oneSuccessOneFailure : "동시 주문에서 하나는 성공하고 하나는 실패해야 합니다. Status1: " + status1 + ", Status2: " + status2;

        executor.shutdown();
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 주문 시 오류가 발생한다")
    void createOrder_NonExistentProduct_Failure() throws Exception {

        // given
        Long memberUid = createTestMember("testUser4");

        // 존재하지 않는 상품 ID로 주문
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest(999L, 1L);
        OrderRequest orderRequest = new OrderRequest(List.of(item));

        // when & then
        mockMvc.perform(post("/api/v1/order/orders")
                        .header("X-Member-Uid", memberUid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("잘못된 주문 데이터로 요청 시 validation 오류가 발생한다")
    void createOrder_ValidationError() throws Exception {
        // given
        Long memberUid = createTestMember("testUser5");

        // 빈 주문 아이템 리스트
        OrderRequest invalidRequest = new OrderRequest(List.of());

        // when & then
        mockMvc.perform(post("/api/v1/order/orders")
                        .header("X-Member-Uid", memberUid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 주문 시 오류가 발생한다")
    void createOrder_NonExistentMember_Failure() throws Exception {
        // given
        Long nonExistentMemberUid = 99999L;
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest(1L, 1L);
        OrderRequest orderRequest = new OrderRequest(List.of(item));

        // when & then
        mockMvc.perform(post("/api/v1/order/orders")
                        .header("X-Member-Uid", nonExistentMemberUid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("존재하지 않는 주문 취소 시 오류가 발생한다")
    void cancelOrder_NonExistentOrder_Failure() throws Exception {
        // given
        Long memberUid = createTestMember("testUser6");
        Long nonExistentOrderId = 99999L;

        // when & then
        mockMvc.perform(post("/api/v1/order/orders/{orderId}/cancel", nonExistentOrderId)
                        .header("X-Member-Uid", memberUid))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    private Long createTestMember(String loginId) throws Exception {
        MemberSignupRequest signupRequest = new MemberSignupRequest(
                loginId,
                "password123!",
                "정기혁",
                "010-2248-0405",
                "M",
                "1990-01-01"
        );

        String response = mockMvc.perform(post("/api/v1/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.path("data").path("memberUid").asLong();
    }
}
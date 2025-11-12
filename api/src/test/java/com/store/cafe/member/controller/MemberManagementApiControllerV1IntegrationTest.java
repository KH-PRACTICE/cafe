package com.store.cafe.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.cafe.member.dto.MemberSignupRequest;
import com.store.cafe.member.dto.MemberWithdrawalCancelRequest;
import com.store.cafe.member.dto.MemberWithdrawalRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberManagementApiControllerV1IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("실제 회원가입 API를 통해 회원가입이 성공적으로 처리된다")
    void signup_RealIntegration_Success() throws Exception {

        // given
        MemberSignupRequest request = new MemberSignupRequest(
                "realUser123",
                "password123!",
                "정기혁",
                "010-2248-0405",
                "M",
                "1995-04-05"
        );

        // when & then
        mockMvc.perform(post("/api/v1/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resultMessage").value("SUCCESS"))
                .andExpect(jsonPath("$.data.loginId").value("realUser123"))
                .andExpect(jsonPath("$.data.memberUid").exists())
                .andExpect(jsonPath("$.data.joinDate").exists());
    }

    @Test
    @DisplayName("중복된 로그인 ID로 실제 회원가입 시도 시 예외가 발생한다")
    void signup_RealIntegration_DuplicateLoginId() throws Exception {
        // given - 첫 번째 회원 가입
        MemberSignupRequest firstRequest = new MemberSignupRequest(
                "duplicateTest",
                "password123!",
                "정기혁",
                "010-1111-1111",
                "M",
                "1990-04-05"
        );

        // 첫 번째 회원가입 성공
        mockMvc.perform(post("/api/v1/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk());

        // 두 번째 회원가입 요청 (중복 로그인 ID)
        MemberSignupRequest duplicateRequest = new MemberSignupRequest(
                "duplicateTest", // 같은 로그인 ID
                "password456!",
                "박기혁",
                "010-2222-2222",
                "M",
                "1995-01-01"
        );

        // when & then - 중복 로그인 ID 예외 확인
        mockMvc.perform(post("/api/v1/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("허용되지 않은 데이터로 회원가입 요청 시 validation 오류가 발생한다")
    void signup_RealIntegration_ValidationError() throws Exception {
        // given - 잘못된 형식의 요청
        MemberSignupRequest invalidRequest = new MemberSignupRequest(
                "ab",
                "123",
                "",
                "invalid-phone",
                "X",
                "invalid-date"
        );

        // when & then
        mockMvc.perform(post("/api/v1/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 후 탈퇴까지 성공적으로 처리된다")
    void withdraw_Success() throws Exception {
        MemberSignupRequest signupRequest = new MemberSignupRequest(
                "testUser",
                "password123!",
                "정기혁",
                "010-3333-3333",
                "M",
                "1995-04-05"
        );

        String signupResponse = mockMvc.perform(post("/api/v1/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(signupResponse);
        Long memberUid = jsonNode.path("data").path("memberUid").asLong();

        MemberWithdrawalRequest withdrawRequest = new MemberWithdrawalRequest(
                "서비스 불만족", ZonedDateTime.now());

        mockMvc.perform(post("/api/v1/member/withdraw")
                        .header("X-Member-Uid", memberUid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resultMessage").value("SUCCESS"))
                .andExpect(jsonPath("$.data.memberUid").value(memberUid));
    }

    @Test
    @DisplayName("회원가입 -> 회원탈퇴  -> 회원탈퇴 취소 까지 성공적으로 처리된다")
    void cancelWithdrawal_RealIntegration_Success() throws Exception {
        // given
        // 1. 회원가입
        MemberSignupRequest signupRequest = new MemberSignupRequest(
                "testUser",
                "password123!",
                "정기혁",
                "010-2248-0405",
                "M",
                "1995-04-05"
        );

        String signupResponse = mockMvc.perform(post("/api/v1/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(signupResponse);
        Long memberUid = jsonNode.path("data").path("memberUid").asLong();

        // 2. 탈퇴 신청
        MemberWithdrawalRequest withdrawRequest = new MemberWithdrawalRequest(
                "재가입 예정",
                ZonedDateTime.now()
        );

        mockMvc.perform(post("/api/v1/member/withdraw")
                        .header("X-Member-Uid", memberUid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk());

        // 탈퇴 취소 요청
        MemberWithdrawalCancelRequest cancelRequest = new MemberWithdrawalCancelRequest(memberUid);

        // when & then - 실제 탈퇴 취소 API 호출
        mockMvc.perform(post("/api/v1/member/withdraw/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resultMessage").value("SUCCESS"))
                .andExpect(jsonPath("$.data.memberUid").value(memberUid))
                .andExpect(jsonPath("$.data.canceledAt").exists());
    }

    @Test
    @DisplayName("잘못된 데이터로 탈퇴 취소 호출 시 validation 오류가 발생한다")
    void cancelWithdrawal_ValidationError() throws Exception {
        // given - 잘못된 탈퇴 취소 요청 (memberUid가 null)
        MemberWithdrawalCancelRequest invalidRequest = new MemberWithdrawalCancelRequest(null);

        // when & then - validation 오류 확인
        mockMvc.perform(post("/api/v1/member/withdraw/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대한 탈퇴 요청 시 오류가 발생한다")
    void withdraw_NotExistingMember_Error() throws Exception {

        // given - 존재하지 않는 회원 ID
        Long nonExistentMemberUid = 99999L;
        MemberWithdrawalRequest withdrawRequest = new MemberWithdrawalRequest(
                "재가입 예정",
                ZonedDateTime.now()
        );

        // when & then - 존재하지 않는 회원 탈퇴 시도
        mockMvc.perform(post("/api/v1/member/withdraw")
                        .header("X-Member-Uid", nonExistentMemberUid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대한 탈퇴 취소 요청 시 오류가 발생한다")
    void cancelWithdrawal_NotExistingMember_Error() throws Exception {
        // given - 존재하지 않는 회원 ID
        Long nonExistentMemberUid = 99999L;
        MemberWithdrawalCancelRequest cancelRequest = new MemberWithdrawalCancelRequest(nonExistentMemberUid);

        // when & then - 존재하지 않는 회원 탈퇴 취소 시도
        mockMvc.perform(post("/api/v1/member/withdraw/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
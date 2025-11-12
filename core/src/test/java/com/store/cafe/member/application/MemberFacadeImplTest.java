package com.store.cafe.member.application;

import com.store.cafe.member.application.command.MemberSignupCommand;
import com.store.cafe.member.application.command.MemberWithdrawalCommand;
import com.store.cafe.member.application.result.MemberSignupResult;
import com.store.cafe.member.application.result.MemberWithdrawalCancelResult;
import com.store.cafe.member.application.result.MemberWithdrawalResult;
import com.store.cafe.member.domain.event.MemberWithdrawalEvent;
import com.store.cafe.member.domain.model.enums.WithdrawalStatus;
import com.store.cafe.member.domain.service.MemberSignupService;
import com.store.cafe.member.domain.service.MemberWithdrawService;
import com.store.cafe.util.DateUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberFacadeImplTest {

    @Mock
    private MemberSignupService memberSignupService;

    @Mock
    private MemberWithdrawService memberWithdrawService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MemberFacadeImpl memberFacade;

    @Test
    @DisplayName("회원가입을 성공적으로 처리한다")
    void signup_Success() {
        // given
        MemberSignupCommand command = new MemberSignupCommand(
                "testUser",
                "password123!",
                "정기혁",
                "010-2248-0405",
                "M",
                "1995-04-05"

        );

        MemberSignupResult expectedResult = new MemberSignupResult(
                1L,
                "testUser",
                DateUtil.now()
        );

        given(memberSignupService.signup(command)).willReturn(expectedResult);

        // when
        MemberSignupResult result = memberFacade.signup(command);

        // then
        assertThat(result).isEqualTo(expectedResult);
        assertThat(result.loginId()).isEqualTo("testUser");
        assertThat(result.memberUid()).isEqualTo(1L);

        then(memberSignupService).should(times(1)).signup(command);
        then(eventPublisher).should(never()).publishEvent(any());
    }

    @Test
    @DisplayName("회원 탈퇴를 처리하고 이벤트를 발행한다")
    void withdrawal_Success() {
        // given
        Long memberUid = 1L;
        String reason = "재가입";
        ZonedDateTime requestedAt = DateUtil.now();
        MemberWithdrawalCommand command = new MemberWithdrawalCommand(reason, requestedAt);

        MemberWithdrawalResult expectedResult = MemberWithdrawalResult.of(
                memberUid,
                WithdrawalStatus.REQUESTED,
                "loginIdHash123",
                requestedAt,
                requestedAt.plusDays(30)
        );

        given(memberWithdrawService.withdraw(memberUid, reason, requestedAt)).willReturn(expectedResult);

        // when
        MemberWithdrawalResult result = memberFacade.withdrawal(memberUid, command);

        // then
        assertThat(result).isEqualTo(expectedResult);
        assertThat(result.memberUid()).isEqualTo(memberUid);
        assertThat(result.loginIdHash()).isEqualTo("loginIdHash123");
        assertThat(result.status()).isEqualTo(WithdrawalStatus.REQUESTED);

        then(memberWithdrawService).should(times(1)).withdraw(memberUid, reason, requestedAt);

        // 이벤트 발행 확인
        ArgumentCaptor<MemberWithdrawalEvent> eventCaptor = ArgumentCaptor.forClass(MemberWithdrawalEvent.class);
        then(eventPublisher).should(times(1)).publishEvent(eventCaptor.capture());

        MemberWithdrawalEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.getMemberUid()).isEqualTo(memberUid);
        assertThat(publishedEvent.getLoginIdHash()).isEqualTo("loginIdHash123");
        assertThat(publishedEvent.getWithdrawalStatus()).isEqualTo(WithdrawalStatus.REQUESTED);
    }

    @Test
    @DisplayName("회원 탈퇴 취소를 처리하고 이벤트를 발행한다")
    void cancelWithdrawal_Success() {
        // given
        Long memberUid = 1L;
        ZonedDateTime fixedDateTime = ZonedDateTime.now();

        MemberWithdrawalCancelResult expectedResult = new MemberWithdrawalCancelResult(
                memberUid,
                "loginIdHash123",
                fixedDateTime
        );

        given(memberWithdrawService.cancelWithdraw(eq(memberUid), any(ZonedDateTime.class))).willReturn(expectedResult);

        // when
        try (MockedStatic<DateUtil> mockedDateUtil = mockStatic(DateUtil.class)) {
            mockedDateUtil.when(DateUtil::now).thenReturn(fixedDateTime);

            MemberWithdrawalCancelResult result = memberFacade.cancelWithdrawal(memberUid);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.memberUid()).isEqualTo(memberUid);
            assertThat(result.loginIdHash()).isEqualTo("loginIdHash123");

            then(memberWithdrawService).should(times(1)).cancelWithdraw(memberUid, fixedDateTime);

            // 이벤트 발행 확인
            ArgumentCaptor<MemberWithdrawalEvent> eventCaptor = ArgumentCaptor.forClass(MemberWithdrawalEvent.class);
            then(eventPublisher).should(times(1)).publishEvent(eventCaptor.capture());

            MemberWithdrawalEvent publishedEvent = eventCaptor.getValue();
            assertThat(publishedEvent.getMemberUid()).isEqualTo(memberUid);
            assertThat(publishedEvent.getLoginIdHash()).isEqualTo("loginIdHash123");
            assertThat(publishedEvent.getWithdrawalStatus()).isEqualTo(WithdrawalStatus.CANCELED);
        }
    }
}
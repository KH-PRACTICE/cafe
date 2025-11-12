package com.store.cafe.member.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.store.cafe.member.application.result.MemberWithdrawalCancelResult;
import com.store.cafe.member.application.result.MemberWithdrawalResult;
import com.store.cafe.member.domain.exception.CannotWithdrawCancelException;
import com.store.cafe.member.domain.exception.CannotWithdrawException;
import com.store.cafe.member.domain.exception.MemberNotFoundException;
import com.store.cafe.member.domain.model.entity.MemberIdentity;
import com.store.cafe.member.domain.model.entity.MemberIdentityRepository;
import com.store.cafe.member.domain.model.entity.MemberStatus;
import com.store.cafe.member.domain.model.entity.MemberStatusRepository;
import com.store.cafe.member.domain.model.entity.MemberWithdrawalRepository;
import com.store.cafe.member.domain.model.entity.MemberWithdrawalSummary;
import com.store.cafe.member.domain.model.enums.MemberStatusType;
import com.store.cafe.member.domain.model.enums.WithdrawalStatus;
import com.store.cafe.util.DateUtil;
import com.store.cafe.util.HashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MemberWithdrawServiceTest {

    @Mock
    private MemberIdentityRepository memberIdentityRepository;

    @Mock
    private MemberStatusRepository memberStatusRepository;

    @Mock
    private MemberWithdrawalRepository memberWithdrawalRepository;

    @InjectMocks
    private MemberWithdrawService memberWithdrawService;

    private Long testMemberUid;
    private String testReason;
    private String testLoginId;

    @BeforeEach
    void setUp() {
        testMemberUid = 1L;
        testReason = "재가입 예정";
        testLoginId = "testuser123";
    }

    @Test
    @DisplayName("최초 탈퇴 요청인 경우 신규 탈퇴 summary 데이터 생성")
    void withdraw_case1() {
        MemberIdentity memberIdentity = MemberIdentity.of(testLoginId);
        MemberStatus memberStatus = MemberStatus.of(testMemberUid, MemberStatusType.ACTIVE);
        String hashedLoginId = "hashedLoginId";

        MemberWithdrawalSummary savedWithdrawal = MemberWithdrawalSummary.of(
                testMemberUid,
                hashedLoginId,
                WithdrawalStatus.REQUESTED,
                testReason,
                DateUtil.now()
        );

        when(memberIdentityRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(memberIdentity));
        when(memberStatusRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(memberStatus));
        when(memberWithdrawalRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.empty());

        try (MockedStatic<HashUtil> hashUtilMock = mockStatic(HashUtil.class)) {
            hashUtilMock.when(() -> HashUtil.hash(testLoginId)).thenReturn(hashedLoginId);

            when(memberWithdrawalRepository.save(any(MemberWithdrawalSummary.class)))
                    .thenReturn(savedWithdrawal);

            MemberWithdrawalResult result = memberWithdrawService.withdraw(testMemberUid, testReason, DateUtil.now());

            assertThat(result).isNotNull();
            assertThat(result.memberUid()).isEqualTo(testMemberUid);
            assertThat(result.status()).isEqualTo(WithdrawalStatus.REQUESTED);
            assertThat(memberStatus.getStatus()).isEqualTo(MemberStatusType.WITHDRAW_PROCESS);

            verify(memberWithdrawalRepository).save(any(MemberWithdrawalSummary.class));
        }
    }

    @Test
    @DisplayName("기존 탈퇴 요청 이력이 존재하는 경우 재탈퇴 요청 시 기존 데이터 업데이트")
    void withdraw_case2() {
        MemberIdentity memberIdentity = MemberIdentity.of(testLoginId);
        MemberStatus memberStatus = MemberStatus.of(testMemberUid, MemberStatusType.ACTIVE);

        MemberWithdrawalSummary existingWithdrawal = MemberWithdrawalSummary.of(
                testMemberUid,
                "hashedLoginId",
                WithdrawalStatus.CANCELED,
                "이전 탈퇴 사유",
                DateUtil.now()
        );

        when(memberIdentityRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(memberIdentity));
        when(memberStatusRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(memberStatus));
        when(memberWithdrawalRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(existingWithdrawal));

        MemberWithdrawalResult result = memberWithdrawService.withdraw(testMemberUid, testReason, DateUtil.now());

        assertThat(result).isNotNull();
        assertThat(result.memberUid()).isEqualTo(testMemberUid);
        assertThat(result.status()).isEqualTo(WithdrawalStatus.REQUESTED);
        assertThat(existingWithdrawal.getStatus()).isEqualTo(WithdrawalStatus.REQUESTED);
        assertThat(existingWithdrawal.getCanceledAt()).isNull();
        assertThat(existingWithdrawal.getRequestedAt()).isNotNull();

        verify(memberWithdrawalRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대한 탈퇴 요청인 경우 MemberNotFoundException 발생 ")
    void withdraw_case3() {
        when(memberIdentityRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberWithdrawService.withdraw(testMemberUid, testReason, DateUtil.now()))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage("Member not found for memberUid: " + testMemberUid);
    }

    @Test
    @DisplayName("회원 상태가 존재하지 않는 경우 MemberNotFoundException 발생")
    void withdraw_case4() {
        MemberIdentity memberIdentity = MemberIdentity.of(testLoginId);

        when(memberIdentityRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(memberIdentity));
        when(memberStatusRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberWithdrawService.withdraw(testMemberUid, testReason, DateUtil.now()))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage("Member not found for memberUid: " + testMemberUid);
    }

    @Test
    @DisplayName("이미 탈퇴가 진행중인 회원에 대한 탈퇴 요청인 경우 CannotWithdrawException 발생")
    void withdraw_case5() {
        MemberIdentity memberIdentity = MemberIdentity.of(testLoginId);
        MemberStatus withdrawProcessStatus = MemberStatus.of(
                testMemberUid,
                MemberStatusType.WITHDRAW_PROCESS
        );

        when(memberIdentityRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(memberIdentity));
        when(memberStatusRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(withdrawProcessStatus));

        assertThatThrownBy(() -> memberWithdrawService.withdraw(testMemberUid, testReason, DateUtil.now()))
                .isInstanceOf(CannotWithdrawException.class)
                .hasMessage("Cannot withdraw because withdrawal is already in progress");
    }

    @Test
    @DisplayName("탈퇴 요청 30일 이내 취소 요청으로 탈퇴 취소 성공")
    void cancelWithdraw_case1() {
        MemberStatus memberStatus = MemberStatus.of(testMemberUid, MemberStatusType.WITHDRAW_PROCESS);
        MemberWithdrawalSummary withdrawalSummary = MemberWithdrawalSummary.of(
                testMemberUid,
                "hashedLoginId",
                WithdrawalStatus.REQUESTED,
                testReason,
                DateUtil.now()
        );

        when(memberStatusRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(memberStatus));
        when(memberWithdrawalRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(withdrawalSummary));

        MemberWithdrawalCancelResult result = memberWithdrawService.cancelWithdraw(testMemberUid, DateUtil.now());

        assertThat(result).isNotNull();
        assertThat(result.memberUid()).isEqualTo(testMemberUid);
        assertThat(result.canceledAt()).isNotNull();
        assertThat(memberStatus.getStatus()).isEqualTo(MemberStatusType.ACTIVE);
        assertThat(withdrawalSummary.getStatus()).isEqualTo(WithdrawalStatus.CANCELED);
        assertThat(withdrawalSummary.getRequestedAt()).isNull();
        assertThat(withdrawalSummary.getCanceledAt()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대한 탈퇴 취소 요청인 경우 MemberNotFoundException 발생 ")
    void cancelWithdraw_case2() {
        when(memberStatusRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberWithdrawService.cancelWithdraw(testMemberUid, DateUtil.now()))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage("Member not found for memberUid: " + testMemberUid);
    }

    @Test
    @DisplayName("탈퇴 요청 이력이 존재하지 않는 회원에 대한 탈퇴 취소 요청인 경우 CannotWithdrawCancelException 발생")
    void cancelWithdraw_case3() {
        MemberStatus memberStatus = MemberStatus.of(testMemberUid, MemberStatusType.WITHDRAW_PROCESS);

        when(memberStatusRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(memberStatus));
        when(memberWithdrawalRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberWithdrawService.cancelWithdraw(testMemberUid, DateUtil.now()))
                .isInstanceOf(CannotWithdrawCancelException.class)
                .hasMessage("Can not cancel withdrawal for memberUid: " + testMemberUid);
    }

    @Test
    @DisplayName("탈퇴 요청 30일 경과로 탈퇴 취소 불가능한 경우 CannotWithdrawCancelException 발생")
    void cancelWithdraw_case4() {
        MemberStatus memberStatus = MemberStatus.of(testMemberUid, MemberStatusType.WITHDRAW_PROCESS);
        MemberWithdrawalSummary withdrawalSummary = MemberWithdrawalSummary.of(
                testMemberUid,
                "hashedLoginId",
                WithdrawalStatus.REQUESTED,
                testReason,
                DateUtil.now()
        );

        when(memberStatusRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(memberStatus));
        when(memberWithdrawalRepository.findByMemberUid(testMemberUid))
                .thenReturn(Optional.of(withdrawalSummary));

        assertThatThrownBy(() -> memberWithdrawService.cancelWithdraw(testMemberUid, DateUtil.now().plusDays(31)))
                .isInstanceOf(CannotWithdrawCancelException.class)
                .hasMessage("The cancellation period has expired");
    }
}
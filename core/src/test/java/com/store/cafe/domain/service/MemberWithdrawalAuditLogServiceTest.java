package com.store.cafe.domain.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.store.cafe.member.domain.model.entity.MemberWithdrawalAuditLog;
import com.store.cafe.member.domain.model.entity.MemberWithdrawalAuditLogRepository;
import com.store.cafe.member.domain.model.enums.WithdrawalStatus;
import com.store.cafe.member.domain.service.MemberWithdrawalAuditLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberWithdrawalAuditLogServiceTest {

    @Mock
    private MemberWithdrawalAuditLogRepository memberWithdrawalAuditLogRepository;

    @InjectMocks
    private MemberWithdrawalAuditLogService memberWithdrawalAuditLogService;

    @Test
    @DisplayName("회원 탈퇴 감사 로그 (이력) 를 저장한다")
    void saveAuditLog() {
        // given
        Long memberUid = 1L;
        String loginIdHash = "hashedLoginId";
        WithdrawalStatus eventType = WithdrawalStatus.REQUESTED;

        // when
        memberWithdrawalAuditLogService.saveAuditLog(memberUid, loginIdHash, eventType);

        // then
        verify(memberWithdrawalAuditLogRepository).saveMemberWithdrawalAudit(any(MemberWithdrawalAuditLog.class));
    }
}
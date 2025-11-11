package com.store.cafe.member.domain.service;

import com.store.cafe.member.domain.model.entity.MemberWithdrawalAuditLog;
import com.store.cafe.member.domain.model.entity.MemberWithdrawalAuditLogRepository;
import com.store.cafe.member.domain.model.enums.WithdrawalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberWithdrawalAuditLogService {

    private final MemberWithdrawalAuditLogRepository memberWithdrawalAuditLogRepository;

    @Transactional
    public void saveAuditLog(Long memberUid, String loginIdHash, WithdrawalStatus eventType) {
        MemberWithdrawalAuditLog auditLog = MemberWithdrawalAuditLog.of(memberUid, loginIdHash, eventType);
        memberWithdrawalAuditLogRepository.saveMemberWithdrawalAudit(auditLog);
    }
}
package com.store.cafe.member.domain.model.entity;

public interface MemberWithdrawalAuditLogRepository {

    void saveMemberWithdrawalAudit(MemberWithdrawalAuditLog memberWithdrawalAuditLog);
}
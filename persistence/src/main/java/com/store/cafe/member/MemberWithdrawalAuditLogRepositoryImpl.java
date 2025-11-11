package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberWithdrawalAuditLog;
import com.store.cafe.member.domain.model.entity.MemberWithdrawalAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberWithdrawalAuditLogRepositoryImpl implements MemberWithdrawalAuditLogRepository {

    private final MemberWithdrawalAuditLogJpaRepository memberWithdrawalAuditLogJpaRepository;

    @Override
    public void saveMemberWithdrawalAudit(MemberWithdrawalAuditLog memberWithdrawalAuditLog) {
        memberWithdrawalAuditLogJpaRepository.save(memberWithdrawalAuditLog);
    }
}
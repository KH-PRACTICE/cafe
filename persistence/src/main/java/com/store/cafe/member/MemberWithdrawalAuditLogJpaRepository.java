package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberWithdrawalAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberWithdrawalAuditLogJpaRepository extends JpaRepository<MemberWithdrawalAuditLog, Long> {
}
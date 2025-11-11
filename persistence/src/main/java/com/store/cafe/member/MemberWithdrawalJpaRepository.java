package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberWithdrawalSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberWithdrawalJpaRepository extends JpaRepository<MemberWithdrawalSummary, Long> {
}
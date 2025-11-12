package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberWithdrawalAuditLog;
import com.store.cafe.member.domain.model.entity.MemberWithdrawalAuditLogRepository;
import com.store.cafe.member.domain.model.enums.WithdrawalStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({MemberWithdrawalAuditLogRepositoryImpl.class})
class MemberWithdrawalAuditLogRepositoryImplTest {

    @Autowired
    private MemberWithdrawalAuditLogRepository auditLogRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("MemberWithdrawalAuditLogRepositoryImpl.saveMemberWithdrawalAudit() - REQUESTED 상태의 회원 탈퇴 감사 로그를 저장할 수 있다")
    void saveMemberWithdrawalAudit_requested() {
        // given
        MemberWithdrawalAuditLog auditLog = MemberWithdrawalAuditLog.of(
                100L, 
                "loginIdHash123", 
                WithdrawalStatus.REQUESTED
        );

        // when
        auditLogRepository.saveMemberWithdrawalAudit(auditLog);
        entityManager.flush();

        // then
        MemberWithdrawalAuditLog saved = entityManager.find(MemberWithdrawalAuditLog.class, auditLog.getId());
        assertThat(saved).isNotNull();
        assertThat(saved.getMemberUid()).isEqualTo(100L);
        assertThat(saved.getLoginIdHash()).isEqualTo("loginIdHash123");
        assertThat(saved.getEventType()).isEqualTo(WithdrawalStatus.REQUESTED);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("MemberWithdrawalAuditLogRepositoryImpl.saveMemberWithdrawalAudit() - CANCELED 상태의 회원 탈퇴 감사 로그를 저장할 수 있다")
    void saveMemberWithdrawalAudit_canceled() {
        // given
        MemberWithdrawalAuditLog auditLog = MemberWithdrawalAuditLog.of(
                200L, 
                "loginIdHash200", 
                WithdrawalStatus.CANCELED
        );

        // when
        auditLogRepository.saveMemberWithdrawalAudit(auditLog);
        entityManager.flush();

        // then
        MemberWithdrawalAuditLog saved = entityManager.find(MemberWithdrawalAuditLog.class, auditLog.getId());
        assertThat(saved).isNotNull();
        assertThat(saved.getMemberUid()).isEqualTo(200L);
        assertThat(saved.getLoginIdHash()).isEqualTo("loginIdHash200");
        assertThat(saved.getEventType()).isEqualTo(WithdrawalStatus.CANCELED);
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
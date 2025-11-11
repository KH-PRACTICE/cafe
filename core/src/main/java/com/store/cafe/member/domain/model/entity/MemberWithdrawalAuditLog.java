package com.store.cafe.member.domain.model.entity;

import com.store.cafe.member.domain.model.enums.WithdrawalStatus;
import com.store.cafe.util.DateUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "member_withdrawal_audit_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberWithdrawalAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_uid", nullable = false)
    private Long memberUid;

    @Column(name = "login_id_hash", nullable = false, length = 255)
    private String loginIdHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private WithdrawalStatus eventType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    private MemberWithdrawalAuditLog(Long memberUid, String loginIdHash, WithdrawalStatus eventType) {
        this.memberUid = memberUid;
        this.loginIdHash = loginIdHash;
        this.eventType = eventType;
        this.createdAt = DateUtil.now();
    }

    public static MemberWithdrawalAuditLog of(Long memberUid, String loginIdHash, WithdrawalStatus eventType) {
        return new MemberWithdrawalAuditLog(memberUid, loginIdHash, eventType);
    }
}
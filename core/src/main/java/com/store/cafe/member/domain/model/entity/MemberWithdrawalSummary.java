package com.store.cafe.member.domain.model.entity;

import com.store.cafe.util.DateUtil;
import com.store.cafe.member.domain.model.enums.WithdrawalStatus;
import com.store.cafe.member.domain.exception.CannotWithdrawCancelException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "member_withdrawal_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberWithdrawalSummary {

    private static final int WITHDRAWAL_CANCELABLE_DAYS = 30;

    @Id
    @Column(name = "member_uid")
    private Long memberUid;

    @Column(name = "login_id_hash", nullable = false)
    private String loginIdHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private WithdrawalStatus status;

    @Column(name = "requested_at")
    private ZonedDateTime requestedAt;

    @Column(name = "canceled_at")
    private ZonedDateTime canceledAt;

    @Column(name = "confirmed_at")
    private ZonedDateTime confirmedAt;

    @Column(name = "scheduled_delete_at")
    private ZonedDateTime scheduledDeleteAt;

    @Column(name = "reason")
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    private MemberWithdrawalSummary(Long memberUid, String loginIdHash, WithdrawalStatus status, String reason, ZonedDateTime now) {
        this.memberUid = memberUid;
        this.loginIdHash = loginIdHash;
        this.status = status;
        this.reason = reason;
        this.createdAt = now;
        this.updatedAt = now;

        if (status == WithdrawalStatus.REQUESTED) {
            this.requestedAt = now;
            this.scheduledDeleteAt = now.plusDays(WITHDRAWAL_CANCELABLE_DAYS + 1);
        }
    }

    public static MemberWithdrawalSummary of(Long memberUid, String loginIdHash, WithdrawalStatus withdrawalStatus,
                                             String reason, ZonedDateTime now) {

        return new MemberWithdrawalSummary(memberUid, loginIdHash, withdrawalStatus, reason, now);
    }

    public void withdrawal(ZonedDateTime now) {

        this.status = WithdrawalStatus.REQUESTED;
        this.requestedAt = now;
        this.scheduledDeleteAt = now.plusDays(WITHDRAWAL_CANCELABLE_DAYS);
        this.canceledAt = null;
    }

    public void cancelWithdrawal(ZonedDateTime now) {

        validateCancelable(now);

        this.status = WithdrawalStatus.CANCELED;
        this.canceledAt = now;
        this.requestedAt = null;
        this.scheduledDeleteAt = null;
        this.reason = null;
    }

    private void validateCancelable(ZonedDateTime now) {
        if (this.status != WithdrawalStatus.REQUESTED) {
            throw new CannotWithdrawCancelException("Cannot cancel withdrawal in current status");
        }

        if (now.isAfter(this.scheduledDeleteAt)) {
            throw new CannotWithdrawCancelException("The cancellation period has expired");
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = DateUtil.now();
    }
}
package com.store.cafe.member.domain.model.entity;

import com.store.cafe.member.domain.model.enums.MemberStatusType;
import com.store.cafe.util.DateUtil;
import com.store.cafe.member.domain.exception.CannotWithdrawException;
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
@Table(name = "member_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberStatus {

    @Id
    @Column(name = "member_uid")
    private Long memberUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private MemberStatusType status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    private MemberStatus(Long memberUid, MemberStatusType status) {

        ZonedDateTime now = DateUtil.now();

        this.memberUid = memberUid;
        this.status = status;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static MemberStatus of(Long memberUid, MemberStatusType status) {
        return new MemberStatus(memberUid, status);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = DateUtil.now();
    }

    public void startWithdrawal() {

        if (status == MemberStatusType.WITHDRAW_PROCESS) {
            throw new CannotWithdrawException("Cannot withdraw because withdrawal is already in progress");
        }

        if (!status.isActive()) {
            throw new CannotWithdrawException("Cannot withdraw in current status");
        }

        this.status = MemberStatusType.WITHDRAW_PROCESS;
    }


    public void startCancelWithdrawal() {

        if (status.isActive()) {
            throw new CannotWithdrawException("Cannot cancel withdrawal in current status");
        }

        this.status = MemberStatusType.ACTIVE;
    }
}
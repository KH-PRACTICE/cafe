package com.store.cafe.member.domain.model.entity;

import com.store.cafe.util.DateUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "member_password")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPassword {

    @Id
    @Column(name = "member_uid")
    private Long memberUid;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "update_dt", nullable = false)
    private ZonedDateTime updatedAt;

    private MemberPassword(Long memberUid, String passwordHash) {

        ZonedDateTime now = DateUtil.now();

        this.memberUid = memberUid;
        this.passwordHash = passwordHash;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static MemberPassword of(Long memberUid, String encode) {
        return new MemberPassword(memberUid, encode);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = DateUtil.now();
    }
}
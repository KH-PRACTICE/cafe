package com.store.cafe.member.domain.model.entity;

import com.store.cafe.util.DateUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "member_identity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_uid")
    private Long memberUid;

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "update_dt", nullable = false)
    private ZonedDateTime updateDt;

    private MemberIdentity(String loginId) {

        ZonedDateTime now = DateUtil.now();

        this.loginId = loginId;
        this.createdAt = now;
        this.updateDt = now;
    }

    public static MemberIdentity of(String loginId) {
        return new MemberIdentity(loginId);
    }

    @PreUpdate
    public void preUpdate() {
        this.updateDt = DateUtil.now();
    }
}
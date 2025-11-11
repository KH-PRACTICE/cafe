package com.store.cafe.member.domain.model.entity;

import com.store.cafe.util.DateUtil;
import com.store.cafe.member.domain.model.enums.MemberGenderCode;
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
@Table(name = "member_private")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPrivate {

    @Id
    @Column(name = "member_uid")
    private Long memberUid;

    @Column(name = "encrypted_name", nullable = false)
    private String encryptedName;

    @Column(name = "encrypted_phone", nullable = false)
    private String encryptedPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private MemberGenderCode gender;

    @Column(name = "encrypted_birth", nullable = false)
    private String encryptedBirth;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "update_dt", nullable = false)
    private ZonedDateTime updatedAt;

    private MemberPrivate(Long memberUid, String encryptedName, String encryptedPhone, MemberGenderCode gender, String encryptedBirth) {

        ZonedDateTime now = DateUtil.now();

        this.memberUid = memberUid;
        this.encryptedName = encryptedName;
        this.encryptedPhone = encryptedPhone;
        this.gender = gender;
        this.encryptedBirth = encryptedBirth;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static MemberPrivate of(Long memberUid, String encryptedName, String encryptedPhone, MemberGenderCode gender, String encryptedBirth) {
        return new MemberPrivate(memberUid, encryptedName, encryptedPhone, gender, encryptedBirth);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = DateUtil.now();
    }
}
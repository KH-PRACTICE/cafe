package com.store.cafe.member.domain.model.enums;

import lombok.Getter;

@Getter
public enum MemberStatusType {

    ACTIVE("활성 회원"),
    WITHDRAW_PROCESS("탈퇴 진행중 회원");

    private final String description;

    MemberStatusType(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

}

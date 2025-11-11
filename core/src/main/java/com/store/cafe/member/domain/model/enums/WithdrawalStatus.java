package com.store.cafe.member.domain.model.enums;

import lombok.Getter;

@Getter
public enum WithdrawalStatus {

    REQUESTED("REQUESTED", "탈퇴 요청한 상태 (탈퇴 진행 중)"),
    CANCELED("CANCELED", "탈퇴 취소한 상태"),
    COMPLETED("COMPLETED", "탈퇴 완료");

    private final String code;
    private final String description;

    WithdrawalStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

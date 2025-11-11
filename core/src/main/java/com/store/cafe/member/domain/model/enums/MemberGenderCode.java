package com.store.cafe.member.domain.model.enums;

import lombok.Getter;

@Getter
public enum MemberGenderCode {

    MALE("M", "남성"),
    FEMALE("F", "여성"),
    OTHER("N", "기타");

    private final String code;
    private final String description;

    MemberGenderCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static MemberGenderCode fromCode(String code) {

        for (MemberGenderCode genderCode : values()) {
            if (genderCode.getCode().equals(code)) {
                return genderCode;
            }
        }

        return OTHER;
    }
}

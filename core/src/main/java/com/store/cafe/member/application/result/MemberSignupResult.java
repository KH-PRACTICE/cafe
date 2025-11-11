package com.store.cafe.member.application.result;

import java.time.ZonedDateTime;

public record MemberSignupResult(
        Long memberUid,
        String loginId,
        ZonedDateTime joinDate
) {
    public static MemberSignupResult of(Long memberUid, String loginId) {
        return new MemberSignupResult(memberUid, loginId, ZonedDateTime.now());
    }
}
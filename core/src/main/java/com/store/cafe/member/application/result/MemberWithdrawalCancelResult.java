package com.store.cafe.member.application.result;

import java.time.ZonedDateTime;

public record MemberWithdrawalCancelResult(
        Long memberUid,
        String loginIdHash,
        ZonedDateTime canceledAt
) {
    public static MemberWithdrawalCancelResult of(Long memberUid, String loginIdHash, ZonedDateTime canceledAt) {
        return new MemberWithdrawalCancelResult(memberUid, loginIdHash, canceledAt);
    }
}

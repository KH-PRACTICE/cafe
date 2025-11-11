package com.store.cafe.member.application.result;

import com.store.cafe.member.domain.model.enums.WithdrawalStatus;

import java.time.ZonedDateTime;

public record MemberWithdrawalResult(
        Long memberUid,
        WithdrawalStatus status,
        String loginIdHash,
        ZonedDateTime requestedAt,
        ZonedDateTime scheduledAt
) {
    public static MemberWithdrawalResult of(Long memberUid, WithdrawalStatus status, String loginIdHash,
                                            ZonedDateTime requestedAt, ZonedDateTime scheduledDeleteAt) {

        return new MemberWithdrawalResult(memberUid, status, loginIdHash, requestedAt, scheduledDeleteAt);
    }
}

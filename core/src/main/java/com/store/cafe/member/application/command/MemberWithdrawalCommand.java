package com.store.cafe.member.application.command;

import java.time.ZonedDateTime;

public record MemberWithdrawalCommand(
        String reason,
        ZonedDateTime requestedAt
) {
}

package com.store.cafe.member.application.command;

public record MemberWithdrawalCommand(
        Long memberUid,
        String reason

) {
}

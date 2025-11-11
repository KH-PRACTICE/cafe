package com.store.cafe.member.application.command;

public record MemberSignupCommand(
        String loginId,
        String password,
        String name,
        String phone,
        String gender,
        String birth
) {
}
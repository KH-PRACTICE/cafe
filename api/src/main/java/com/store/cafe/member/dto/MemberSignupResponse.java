package com.store.cafe.member.dto;

import com.store.cafe.member.application.result.MemberSignupResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "회원가입 응답")
public record MemberSignupResponse(

        @Schema(description = "회원번호", example = "12345")
        Long memberUid,
        
        @Schema(description = "로그인 ID", example = "user123")
        String loginId,
        
        @Schema(description = "가입일시", example = "2023-01-01T10:00:00+09:00")
        ZonedDateTime joinDate
) {
    public static MemberSignupResponse from(MemberSignupResult signupResult) {
        return new MemberSignupResponse(
                signupResult.memberUid(),
                signupResult.loginId(),
                signupResult.joinDate()
        );
    }
}

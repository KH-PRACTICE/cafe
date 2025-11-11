package com.store.cafe.member.dto;

import com.store.cafe.member.application.result.MemberWithdrawalResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "회원탈퇴 응답")
public record MemberWithdrawalResponse(

        @Schema(description = "회원번호", example = "12345")
        Long memberUid,
        
        @Schema(description = "탈퇴 신청일시", example = "2025-11-081T10:00:00+09:00")
        ZonedDateTime requestedAt,
        
        @Schema(description = "탈퇴 예정일시", example = "2023-12-08T10:00:00+09:00")
        ZonedDateTime scheduledAt
) {
    public static MemberWithdrawalResponse from(MemberWithdrawalResult result) {
        return new MemberWithdrawalResponse(
                result.memberUid(),
                result.requestedAt(),
                result.scheduledAt()
        );
    }
}

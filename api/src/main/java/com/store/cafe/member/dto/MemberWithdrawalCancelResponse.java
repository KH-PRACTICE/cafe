package com.store.cafe.member.dto;

import com.store.cafe.member.application.result.MemberWithdrawalCancelResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "회원탈퇴 취소 응답")
public record MemberWithdrawalCancelResponse(
        @Schema(description = "회원번호", example = "12345")
        Long memberUid,
        
        @Schema(description = "취소일시", example = "2025-11-08T15:30:00+09:00")
        ZonedDateTime canceledAt
) {
    public static MemberWithdrawalCancelResponse from(MemberWithdrawalCancelResult result) {
        return new MemberWithdrawalCancelResponse(
                result.memberUid(),
                result.canceledAt()
        );
    }
}

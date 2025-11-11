package com.store.cafe.member.dto;

import com.store.cafe.member.application.command.MemberWithdrawalCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Schema(description = "회원탈퇴 요청")
public record MemberWithdrawalRequest(

        @Schema(description = "탈퇴 사유", example = "재가입 예정입니다.")
        @NotBlank(message = "탈퇴 사유는 필수입니다.")
        String reason,

        @Schema(description = "탈퇴 요청 시각", example = "2025-11-11T12:00:00Z")
        @NotNull(message = "탈퇴 요청 시각은 필수입니다.")
        ZonedDateTime requestedAt

) {
    public MemberWithdrawalCommand toCommand() {
        return new MemberWithdrawalCommand(
                this.reason,
                this.requestedAt
        );
    }
}

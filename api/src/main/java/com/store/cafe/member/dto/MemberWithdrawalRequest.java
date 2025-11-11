package com.store.cafe.member.dto;

import com.store.cafe.member.application.command.MemberWithdrawalCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "회원탈퇴 요청")
public record MemberWithdrawalRequest(

        @Schema(description = "회원번호", example = "12345")
        @NotNull(message = "회원번호는 필수입니다.")
        Long memberUid,

        @Schema(description = "탈퇴 사유", example = "재가입 예정입니다.")
        @NotBlank(message = "탈퇴 사유는 필수입니다.")
        String reason
) {
        public MemberWithdrawalCommand toCommand() {
                return new MemberWithdrawalCommand(
                        this.memberUid,
                        this.reason
                );
        }
}

package com.store.cafe.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

@Schema(description = "회원탈퇴 취소 요청")
public record MemberWithdrawalCancelRequest(

        @Schema(description = "회원번호", example = "1")
        @NotNull(message = "회원번호는 필수입니다.")
        Long memberUid
) {
}

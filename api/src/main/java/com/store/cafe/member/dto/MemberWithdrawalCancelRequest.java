package com.store.cafe.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원탈퇴 취소 요청")
public record MemberWithdrawalCancelRequest(
        @Schema(description = "회원번호", example = "12345")
        Long memberUid
) {
}

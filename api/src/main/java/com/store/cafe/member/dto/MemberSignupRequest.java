package com.store.cafe.member.dto;

import com.store.cafe.member.application.command.MemberSignupCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Schema(description = "회원가입 요청")
public record MemberSignupRequest(

        @Schema(description = "로그인 ID", example = "user123")
        @NotBlank(message = "로그인 ID는 필수입니다")
        @Size(min = 4, max = 20, message = "로그인 ID는 4-20자여야 합니다")
        @Pattern(
                regexp = "^[a-zA-Z0-9_-]+$",
                message = "로그인 ID는 영문, 숫자, '_', '-'만 사용 가능합니다"
        )
        String loginId,

        @Schema(description = "비밀번호", example = "password123!")
        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, max = 20, message = "비밀번호는 8-20자여야 합니다")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
        )
        String password,

        @Schema(description = "이름", example = "정기혁")
        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 50, message = "이름은 2-50자여야 합니다")
        @Pattern(
                regexp = "^[가-힣a-zA-Z]+$",
                message = "이름은 한글 또는 영문만 가능합니다"
        )
        String name,

        @Schema(description = "전화번호", example = "010-2248-0405")
        @NotBlank(message = "전화번호는 필수입니다")
        @Pattern(
                regexp = "^01[0-9]-\\d{4}-\\d{4}$",
                message = "전화번호는 010-2248-0405 형식이어야 합니다"
        )
        String phone,

        @Schema(description = "성별 (M: 남성, F: 여성)", example = "M")
        @NotBlank(message = "성별은 필수입니다")
        @Pattern(
                regexp = "^(M|F)$",
                message = "성별은 M(남성) 또는 F(여성)만 가능합니다"
        )
        String gender,

        @Schema(description = "생년월일", example = "1995-04-05")
        @NotBlank(message = "생년월일은 필수입니다")
        @Pattern(
                regexp = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
                message = "생년월일은 YYYY-MM-DD 형식이어야 합니다"
        )
        String birth
) {

    public MemberSignupCommand toCommand() {
        return new MemberSignupCommand(
                loginId,
                password,
                name,
                phone,
                gender,
                birth
        );
    }
}
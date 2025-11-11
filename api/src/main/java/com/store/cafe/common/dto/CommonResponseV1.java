package com.store.cafe.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 응답 형식")
public record CommonResponseV1<T>(

        @Schema(description = "응답 데이터")
        T data,
        
        @Schema(description = "처리 결과 메시지", example = "SUCCESS")
        String resultMessage
) {

    public static <T> CommonResponseV1<T> success(T data) {
        return new CommonResponseV1<>(data, "SUCCESS");
    }

    public static <T> CommonResponseV1<T> error(String errorMessage) {
        return new CommonResponseV1<>(null, errorMessage);
    }
}

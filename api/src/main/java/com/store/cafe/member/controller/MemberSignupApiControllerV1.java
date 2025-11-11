package com.store.cafe.member.controller;

import com.store.cafe.member.application.MemberFacade;
import com.store.cafe.member.application.result.MemberSignupResult;
import com.store.cafe.common.dto.CommonResponseV1;
import com.store.cafe.member.dto.MemberSignupRequest;
import com.store.cafe.member.dto.MemberSignupResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Tag(name = "회원가입", description = "회원가입 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberSignupApiControllerV1 {

    private final MemberFacade memberFacade;
    
    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @PostMapping("/signup")
    public CommonResponseV1<MemberSignupResponse> signup(@RequestBody @Valid MemberSignupRequest request) {

        MemberSignupResult signupResult = memberFacade.signup(request.toCommand());
        return CommonResponseV1.success(MemberSignupResponse.from(signupResult));
    }
}
package com.store.cafe.member.controller;

import com.store.cafe.member.application.MemberFacade;
import com.store.cafe.member.application.result.MemberSignupResult;
import com.store.cafe.common.dto.CommonResponseV1;
import com.store.cafe.member.application.result.MemberWithdrawalCancelResult;
import com.store.cafe.member.application.result.MemberWithdrawalResult;
import com.store.cafe.member.dto.MemberSignupRequest;
import com.store.cafe.member.dto.MemberSignupResponse;
import com.store.cafe.member.dto.MemberWithdrawalCancelRequest;
import com.store.cafe.member.dto.MemberWithdrawalCancelResponse;
import com.store.cafe.member.dto.MemberWithdrawalRequest;
import com.store.cafe.member.dto.MemberWithdrawalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Tag(name = "회원 관리", description = "회원가입 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberManagementApiControllerV1 {

    private final MemberFacade memberFacade;
    
    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @PostMapping("/signup")
    public CommonResponseV1<MemberSignupResponse> signup(@RequestBody @Valid MemberSignupRequest request) {

        MemberSignupResult signupResult = memberFacade.signup(request.toCommand());
        return CommonResponseV1.success(MemberSignupResponse.from(signupResult));
    }

    @Operation(summary = "회원탈퇴", description = "회원탈퇴를 신청합니다.")
    @PostMapping("/withdraw")
    public CommonResponseV1<MemberWithdrawalResponse> withdraw(@RequestHeader("X-Member-Uid") Long memberUid,
                                                               @RequestBody @Valid MemberWithdrawalRequest request) {

        MemberWithdrawalResult result = memberFacade.withdrawal(memberUid, request.toCommand());
        return CommonResponseV1.success(MemberWithdrawalResponse.from(result));
    }

    @Operation(summary = "회원탈퇴 취소", description = "회원탈퇴를 취소합니다.")
    @PostMapping("/withdraw/cancel")
    public CommonResponseV1<MemberWithdrawalCancelResponse> cancelWithdrawal(@RequestBody @Valid MemberWithdrawalCancelRequest request) {

        MemberWithdrawalCancelResult result = memberFacade.cancelWithdrawal(request.memberUid());
        return CommonResponseV1.success(MemberWithdrawalCancelResponse.from(result));
    }
}
package com.store.cafe.member.application;

import com.store.cafe.member.application.command.MemberSignupCommand;
import com.store.cafe.member.application.command.MemberWithdrawalCommand;
import com.store.cafe.member.application.result.MemberSignupResult;
import com.store.cafe.member.application.result.MemberWithdrawalCancelResult;
import com.store.cafe.member.application.result.MemberWithdrawalResult;

public interface MemberFacade {

    /**
     * 회원가입
     * @param command (loginId, password, name, phone, gender, birth)
     * @return MemberSignupResult (memberUid, loginIdHash)
     */
    MemberSignupResult signup(MemberSignupCommand command);

    /**
     * 회원탈퇴 요청
     * @param memberUid 회원고유번호
     * @param command (reason, requestedAt)
     * @return MemberWithdrawalResult (memberUid, loginIdHash, withdrawalRequestedAt)
     */
    MemberWithdrawalResult withdrawal(Long memberUid, MemberWithdrawalCommand command);

    /**
     * 회원탈퇴 요청 취소
     * @param memberUid 회원고유번호
     * @return MemberWithdrawalCancelResult (memberUid, loginIdHash, withdrawalCanceledAt)
     */
    MemberWithdrawalCancelResult cancelWithdrawal(Long memberUid);
}

package com.store.cafe.member.application;

import com.store.cafe.member.application.command.MemberSignupCommand;
import com.store.cafe.member.application.command.MemberWithdrawalCommand;
import com.store.cafe.member.application.result.MemberSignupResult;
import com.store.cafe.member.application.result.MemberWithdrawalCancelResult;
import com.store.cafe.member.application.result.MemberWithdrawalResult;

public interface MemberFacade {

    MemberSignupResult signup(MemberSignupCommand command);

    MemberWithdrawalResult withdrawal(Long memberUid, MemberWithdrawalCommand command);

    MemberWithdrawalCancelResult cancelWithdrawal(Long memberUid);
}

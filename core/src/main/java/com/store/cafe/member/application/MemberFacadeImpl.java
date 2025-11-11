package com.store.cafe.member.application;

import com.store.cafe.member.application.command.MemberSignupCommand;
import com.store.cafe.member.application.command.MemberWithdrawalCommand;
import com.store.cafe.member.application.result.MemberSignupResult;
import com.store.cafe.member.application.result.MemberWithdrawalCancelResult;
import com.store.cafe.member.application.result.MemberWithdrawalResult;
import com.store.cafe.member.domain.event.MemberWithdrawalEvent;
import com.store.cafe.member.domain.model.enums.WithdrawalStatus;
import com.store.cafe.member.domain.service.MemberSignupService;
import com.store.cafe.member.domain.service.MemberWithdrawService;
import com.store.cafe.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberFacadeImpl implements MemberFacade {

    private final MemberSignupService memberSignupService;
    private final MemberWithdrawService memberWithdrawService;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public MemberSignupResult signup(MemberSignupCommand command) {
        return memberSignupService.signup(command);
    }

    @Override
    public MemberWithdrawalResult withdrawal(Long memberUid, MemberWithdrawalCommand command) {

        MemberWithdrawalResult result = memberWithdrawService.withdraw(memberUid, command.reason(), command.requestedAt());
        publishEvent(memberUid, result.loginIdHash(), WithdrawalStatus.REQUESTED);

        return result;
    }

    @Override
    public MemberWithdrawalCancelResult cancelWithdrawal(Long memberUid) {

        MemberWithdrawalCancelResult result = memberWithdrawService.cancelWithdraw(memberUid, DateUtil.now());
        publishEvent(memberUid, result.loginIdHash(), WithdrawalStatus.CANCELED);

        return result;
    }

    private void publishEvent(Long memberUid, String loginIdHash, WithdrawalStatus status) {

        MemberWithdrawalEvent event = MemberWithdrawalEvent.of(
                memberUid,
                loginIdHash,
                status
        );

        eventPublisher.publishEvent(event);
    }
}


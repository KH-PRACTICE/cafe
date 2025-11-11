package com.store.cafe.member.domain.service;

import com.store.cafe.member.application.result.MemberWithdrawalCancelResult;
import com.store.cafe.member.application.result.MemberWithdrawalResult;
import com.store.cafe.member.domain.exception.CannotWithdrawCancelException;
import com.store.cafe.member.domain.exception.MemberNotFoundException;
import com.store.cafe.member.domain.model.entity.MemberIdentity;
import com.store.cafe.member.domain.model.entity.MemberIdentityRepository;
import com.store.cafe.member.domain.model.entity.MemberStatus;
import com.store.cafe.member.domain.model.entity.MemberStatusRepository;
import com.store.cafe.member.domain.model.entity.MemberWithdrawalRepository;
import com.store.cafe.member.domain.model.entity.MemberWithdrawalSummary;
import com.store.cafe.member.domain.model.enums.WithdrawalStatus;
import com.store.cafe.util.DateUtil;
import com.store.cafe.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class MemberWithdrawService {

    private final MemberIdentityRepository memberIdentityRepository;
    private final MemberStatusRepository memberStatusRepository;
    private final MemberWithdrawalRepository memberWithdrawalRepository;

    @Transactional
    public MemberWithdrawalResult withdraw(Long memberUid, String reason, ZonedDateTime requestedAt) {

        MemberIdentity member = findMemberOrThrow(memberUid);
        MemberStatus memberStatus = findMemberStatusOrThrow(memberUid);

        memberStatus.startWithdrawal();

        MemberWithdrawalSummary withdrawalSummary = createOrUpdateWithdrawal(
                memberUid,
                member.getLoginId(),
                reason,
                requestedAt
        );

        return MemberWithdrawalResult.of(
                memberUid,
                withdrawalSummary.getStatus(),
                withdrawalSummary.getLoginIdHash(),
                withdrawalSummary.getRequestedAt(),
                withdrawalSummary.getScheduledDeleteAt()
        );
    }

    private MemberWithdrawalSummary createOrUpdateWithdrawal(
            Long memberUid,
            String loginId,
            String reason,
            ZonedDateTime requestedAt
    ) {
        return memberWithdrawalRepository.findByMemberUid(memberUid)
                .map(existing -> {
                    existing.withdrawal(requestedAt);
                    return existing;
                })
                .orElseGet(() -> {
                    String loginIdHash = HashUtil.hash(loginId);
                    MemberWithdrawalSummary newWithdrawal =
                            MemberWithdrawalSummary.of(
                                    memberUid,
                                    loginIdHash,
                                    WithdrawalStatus.REQUESTED,
                                    reason,
                                    DateUtil.now()
                            );
                    return memberWithdrawalRepository.save(newWithdrawal);
                });
    }

    @Transactional
    public MemberWithdrawalCancelResult cancelWithdraw(Long memberUid, ZonedDateTime canceledAt) {

        MemberStatus memberStatus = findMemberStatusOrThrow(memberUid);

        memberStatus.startCancelWithdrawal();

        MemberWithdrawalSummary withdrawal = cancelWithdrawal(memberUid, canceledAt);

        return MemberWithdrawalCancelResult.of(
                memberUid,
                withdrawal.getLoginIdHash(),
                withdrawal.getCanceledAt()
        );
    }

    private MemberWithdrawalSummary cancelWithdrawal(Long memberUid, ZonedDateTime canceledAt) {

        return memberWithdrawalRepository.findByMemberUid(memberUid)
                .map(existing -> {
                    existing.cancelWithdrawal(canceledAt);
                    return existing;
                })
                .orElseThrow(() -> new CannotWithdrawCancelException("Can not cancel withdrawal for memberUid: " + memberUid));
    }

    private MemberStatus findMemberStatusOrThrow(Long memberUid) {
        return memberStatusRepository.findByMemberUid(memberUid)
                .orElseThrow(() -> new MemberNotFoundException("Member not found for memberUid: " + memberUid));
    }

    private MemberIdentity findMemberOrThrow(Long memberUid) {
        return memberIdentityRepository.findByMemberUid(memberUid)
                .orElseThrow(() -> new MemberNotFoundException("Member not found for memberUid: " + memberUid));
    }
}
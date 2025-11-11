package com.store.cafe.member.domain.event;

import com.store.cafe.member.domain.model.enums.WithdrawalStatus;
import lombok.Getter;

@Getter
public class MemberWithdrawalEvent {
    
    private final Long memberUid;
    private final String loginIdHash;
    private final WithdrawalStatus withdrawalStatus;
    
    public MemberWithdrawalEvent(Long memberUid, String loginIdHash, WithdrawalStatus withdrawalStatus) {
        this.memberUid = memberUid;
        this.loginIdHash = loginIdHash;
        this.withdrawalStatus = withdrawalStatus;
    }
    
    public static MemberWithdrawalEvent of(Long memberUid, String loginIdHash, WithdrawalStatus withdrawalStatus) {
        return new MemberWithdrawalEvent(memberUid, loginIdHash, withdrawalStatus);
    }
}
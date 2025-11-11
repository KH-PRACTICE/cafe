package com.store.cafe.member.domain.model.entity;

import java.util.Optional;

public interface MemberWithdrawalRepository {

    MemberWithdrawalSummary save(MemberWithdrawalSummary memberWithdrawalSummary);
    
    Optional<MemberWithdrawalSummary> findByMemberUid(Long memberUid);

}
package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberWithdrawalRepository;
import com.store.cafe.member.domain.model.entity.MemberWithdrawalSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberWithdrawalRepositoryImpl implements MemberWithdrawalRepository {

    private final MemberWithdrawalJpaRepository memberWithdrawalJpaRepository;

    @Override
    public MemberWithdrawalSummary save(MemberWithdrawalSummary memberWithdrawalSummary) {
        return memberWithdrawalJpaRepository.save(memberWithdrawalSummary);
    }

    @Override
    public Optional<MemberWithdrawalSummary> findByMemberUid(Long memberUid) {
        return memberWithdrawalJpaRepository.findById(memberUid);
    }

}
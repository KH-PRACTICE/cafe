package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberStatus;
import com.store.cafe.member.domain.model.entity.MemberStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberStatusRepositoryImpl implements MemberStatusRepository {

    private final MemberStatusJpaRepository memberStatusJpaRepository;

    @Override
    public void saveMemberStatus(MemberStatus memberStatus) {
        memberStatusJpaRepository.save(memberStatus);
    }

    @Override
    public Optional<MemberStatus> findByMemberUid(Long memberUid) {
        return memberStatusJpaRepository.findById(memberUid);
    }
}

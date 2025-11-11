package com.store.cafe.member.domain.model.entity;

import java.util.Optional;

public interface MemberStatusRepository {

    void saveMemberStatus(MemberStatus memberStatus);

    Optional<MemberStatus> findByMemberUid(Long memberUid);
}

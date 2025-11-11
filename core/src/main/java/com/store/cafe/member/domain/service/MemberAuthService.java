package com.store.cafe.member.domain.service;

import com.store.cafe.member.domain.model.entity.MemberStatus;
import com.store.cafe.member.domain.model.entity.MemberStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 회원 인증 상태 체크 서비스로 가정
 */

@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberStatusRepository memberStatusRepository;

    public boolean isAuth(Long memberUid) {

        Optional<MemberStatus> memberOptional = memberStatusRepository.findByMemberUid(memberUid);

        if (memberOptional.isEmpty()) {
            return false;
        }

        MemberStatus memberStatus = memberOptional.get();

        return memberStatus.isActive();
    }

}

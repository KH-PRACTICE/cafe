package com.store.cafe.member.domain.service;

import com.store.cafe.member.application.command.MemberSignupCommand;
import com.store.cafe.member.application.result.MemberSignupResult;
import com.store.cafe.member.domain.crypto.CryptoService;
import com.store.cafe.member.domain.crypto.PasswordEncoder;
import com.store.cafe.member.domain.exception.DuplicateLoginIdException;
import com.store.cafe.member.domain.model.entity.MemberIdentity;
import com.store.cafe.member.domain.model.entity.MemberIdentityRepository;
import com.store.cafe.member.domain.model.entity.MemberPassword;
import com.store.cafe.member.domain.model.entity.MemberPasswordRepository;
import com.store.cafe.member.domain.model.entity.MemberPrivate;
import com.store.cafe.member.domain.model.entity.MemberPrivateRepository;
import com.store.cafe.member.domain.model.entity.MemberStatus;
import com.store.cafe.member.domain.model.entity.MemberStatusRepository;
import com.store.cafe.member.domain.model.enums.MemberGenderCode;
import com.store.cafe.member.domain.model.enums.MemberStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberSignupService {

    private final MemberIdentityRepository memberIdentityRepository;
    private final MemberPasswordRepository memberPasswordRepository;
    private final MemberPrivateRepository memberPrivateRepository;
    private final MemberStatusRepository memberStatusRepository;

    private final PasswordEncoder passwordEncoder;
    private final CryptoService cryptoService;

    @Transactional
    public MemberSignupResult signup(MemberSignupCommand command) {
        
        try {
            MemberIdentity memberIdentity = saveMemberIdentity(command.loginId());
            Long memberUid = memberIdentity.getMemberUid();

            saveMemberPassword(memberUid, command.password());
            saveMemberPrivate(memberUid, command.name(), command.phone(), command.gender(), command.birth());
            saveMemberStatus(memberUid);

            return MemberSignupResult.of(memberUid, memberIdentity.getLoginId());

        } catch (DataIntegrityViolationException e) {
            throw new DuplicateLoginIdException("Login ID is already in use");
        }
    }

    private MemberIdentity saveMemberIdentity(String loginId) {
        return memberIdentityRepository.save(MemberIdentity.of(loginId));
    }

    private void saveMemberPassword(Long memberUid, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        memberPasswordRepository.save(MemberPassword.of(memberUid, encodedPassword));
    }

    private void saveMemberPrivate(Long memberUid, String name, String phone, String gender, String birth) {
        memberPrivateRepository.save(
                MemberPrivate.of(
                        memberUid,
                        cryptoService.encrypt(name),
                        cryptoService.encrypt(phone),
                        MemberGenderCode.fromCode(gender),
                        cryptoService.encrypt(birth)
                )
        );
    }

    private void saveMemberStatus(Long memberUid) {
        memberStatusRepository.saveMemberStatus(MemberStatus.of(memberUid, MemberStatusType.ACTIVE));
    }
}
package com.store.cafe.domain.service;

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
import com.store.cafe.member.domain.service.MemberSignupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberSignupServiceTest {

    @Mock
    private MemberIdentityRepository memberIdentityRepository;
    
    @Mock
    private MemberPasswordRepository memberPasswordRepository;
    
    @Mock
    private MemberPrivateRepository memberPrivateRepository;
    
    @Mock
    private MemberStatusRepository memberStatusRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private CryptoService cryptoService;

    @InjectMocks
    private MemberSignupService memberSignupService;

    private MemberSignupCommand command;
    private MemberIdentity memberIdentity;

    @BeforeEach
    void setUp() {
        command = new MemberSignupCommand(
                "testUser",
                "password123!",
                "정기혁",
                "010-2248-0405",
                "M",
                "1995-04-05"
        );

        memberIdentity = MemberIdentity.of("testUser");
    }

    @Test
    @DisplayName("회원가입이 성공적으로 처리되어야 한다")
    void signup_Success() {
        // given
        when(memberIdentityRepository.save(any(MemberIdentity.class))).thenReturn(memberIdentity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(cryptoService.encrypt(anyString())).thenReturn("encryptedValue");

        // when
        MemberSignupResult result = memberSignupService.signup(command);

        // then
        assertThat(result.joinDate()).isNotNull();
        assertThat(result.loginId()).isEqualTo("testUser");

        verify(memberIdentityRepository).save(any(MemberIdentity.class));
        verify(memberPasswordRepository).save(any(MemberPassword.class));
        verify(memberPrivateRepository).save(any(MemberPrivate.class));
        verify(memberStatusRepository).saveMemberStatus(any(MemberStatus.class));
    }

    @Test
    @DisplayName("중복된 로그인 ID로 가입 시 예외가 발생해야 한다")
    void signup_DuplicateLoginId_ThrowsException() {
        // given
        when(memberIdentityRepository.save(any(MemberIdentity.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key"));

        // when & then
        assertThatThrownBy(() -> memberSignupService.signup(command))
                .isInstanceOf(DuplicateLoginIdException.class)
                .hasMessage("Login ID is already in use");
    }
}
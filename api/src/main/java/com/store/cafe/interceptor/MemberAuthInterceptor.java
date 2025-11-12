package com.store.cafe.interceptor;

import com.store.cafe.common.exception.AuthenticationException;
import com.store.cafe.member.domain.service.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class MemberAuthInterceptor implements HandlerInterceptor {

    private final MemberAuthService memberAuthService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String memberUidHeader = request.getHeader("X-Member-Uid");

        if (memberUidHeader == null) {
            throw new AuthenticationException("Login required to use this service");
        }

        Long memberUid = Long.parseLong(memberUidHeader);
        boolean memberExists = checkMemberExists(memberUid);

        if (!memberExists) {
            throw new AuthenticationException("Login required to use this service");
        }

        return true;
    }

    private boolean checkMemberExists(Long memberUid) {
        return memberAuthService.isAuth(memberUid);
    }
}
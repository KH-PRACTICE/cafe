package com.store.cafe.member.domain.event;

import com.store.cafe.member.domain.service.MemberWithdrawalAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberWithdrawalAuditEventListener {

    private final MemberWithdrawalAuditLogService memberWithdrawalAuditLogService;

    @EventListener
    public void handleMemberWithdrawalEvent(MemberWithdrawalEvent event) {
        memberWithdrawalAuditLogService.saveAuditLog(
                event.getMemberUid(),
                event.getLoginIdHash(),
                event.getWithdrawalStatus()
        );
    }
}
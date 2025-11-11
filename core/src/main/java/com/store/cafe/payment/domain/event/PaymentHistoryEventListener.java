package com.store.cafe.payment.domain.event;

import com.store.cafe.payment.domain.service.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentHistoryEventListener {

    private final PaymentHistoryService paymentHistoryService;

    @EventListener
    public void handlePaymentEvent(PaymentEvent event) {

        paymentHistoryService.savePaymentOrder(
                event.orderId(),
                event.transactionId(),
                event.status()
        );
    }


}

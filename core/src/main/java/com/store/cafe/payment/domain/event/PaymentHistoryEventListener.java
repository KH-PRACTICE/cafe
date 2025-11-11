package com.store.cafe.payment.domain.event;

import com.store.cafe.payment.domain.service.PaymentOrderHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentHistoryEventListener {

    private final PaymentOrderHistoryService paymentOrderHistoryService;

    @EventListener
    public void handlePaymentEvent(PaymentEvent event) {

        paymentOrderHistoryService.savePaymentOrder(
                event.orderId(),
                event.transactionId(),
                event.status()
        );
    }


}

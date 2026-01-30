package com.peters.cafecart.features.Authentication.events;

import com.peters.cafecart.shared.notification.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class VerificationEmailListener {
    @Autowired EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VerificationEmailEvent event) {
        emailService.sendVerificationEmail(event.email(), event.token());
    }
}

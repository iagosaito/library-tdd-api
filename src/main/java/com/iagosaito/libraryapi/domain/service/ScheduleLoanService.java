package com.iagosaito.libraryapi.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleLoanService {

    private static final String CRON_LOAN_DEBT = "0 0 0 1/1 * ?";
    private static final String EMAIL = "iago@tddcourse.com";

    private final EmailService emailService;

    @Scheduled(cron = CRON_LOAN_DEBT)
    public void notifyLoanDebt() {
        emailService.sendEmail(EMAIL);
    }
}

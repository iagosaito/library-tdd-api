package com.iagosaito.libraryapi.domain.service;

import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendEmail(String email) {
        System.out.println("ENVIANDO E-MAIL PARA: " + email);
    }
}

package com.company.aggregator.service;

import jakarta.mail.MessagingException;

import java.io.FileNotFoundException;

public interface EmailSenderService {
    void sendEmailWithAttachment(String toAddress, String subject, String message, String attachment) throws MessagingException, FileNotFoundException;
}

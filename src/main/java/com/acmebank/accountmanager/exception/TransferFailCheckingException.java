package com.acmebank.accountmanager.exception;

public class TransferFailCheckingException extends RuntimeException {
    public TransferFailCheckingException(String message) {
        super(message);
    }
}
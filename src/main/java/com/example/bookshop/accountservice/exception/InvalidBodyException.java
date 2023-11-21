package com.example.bookshop.accountservice.exception;

public class InvalidBodyException extends RuntimeException{
    public InvalidBodyException(String message) {
        super(message);
    }
}
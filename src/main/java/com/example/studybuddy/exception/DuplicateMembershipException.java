package com.example.studybuddy.exception;

public class DuplicateMembershipException extends RuntimeException {

    public DuplicateMembershipException(String message) {
        super(message);
    }
}
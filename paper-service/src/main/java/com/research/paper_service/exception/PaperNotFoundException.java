package com.research.paper_service.exception;

public class PaperNotFoundException extends RuntimeException {

    public PaperNotFoundException(String message) {
        super(message);
    }

}
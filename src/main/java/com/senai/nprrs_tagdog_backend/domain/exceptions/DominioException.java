package com.senai.nprrs_tagdog_backend.domain.exceptions;

public abstract class DominioException extends RuntimeException {
    public DominioException(String message) {
        super(message);
    }
}

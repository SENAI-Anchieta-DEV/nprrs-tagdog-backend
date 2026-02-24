package com.senai.nprrs_tagdog_backend.domain.exceptions;

public class OperacaoNaoPermitidaException extends DominioException {
    public OperacaoNaoPermitidaException(String message) {
        super(message);
    }
}

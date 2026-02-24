package com.senai.nprrs_tagdog_backend.domain.exceptions;

public class FalhaProcessamentoException extends DominioException {
    public FalhaProcessamentoException() {
        super("Falha ao processar a solicitação");
    }
}

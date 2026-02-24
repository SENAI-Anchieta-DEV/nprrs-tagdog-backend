package com.senai.nprrs_tagdog_backend.domain.exceptions;

public class AcessoNegadoException extends DominioException {
    public AcessoNegadoException() {
        super("Acesso negado");
    }
}

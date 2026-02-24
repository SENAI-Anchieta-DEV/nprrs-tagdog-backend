package com.senai.nprrs_tagdog_backend.domain.exceptions;

public class EntidadeNaoEncontradaException extends DominioException {
    public EntidadeNaoEncontradaException(String entidade) {
        super(entidade+"não encontrada.");
    }
}

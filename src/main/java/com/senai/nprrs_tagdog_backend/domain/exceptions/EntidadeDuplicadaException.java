package com.senai.nprrs_tagdog_backend.domain.exceptions;

public class EntidadeDuplicadaException extends DominioException {
    public EntidadeDuplicadaException(String entidade) {
        super(entidade+" já cadastrada. ");
    }
}

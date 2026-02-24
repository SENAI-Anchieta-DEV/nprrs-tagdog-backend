package com.senai.nprrs_tagdog_backend.domain.exceptions;

public class RecursoIndisponivelException extends DominioException {
    public RecursoIndisponivelException() {
        super("Recursos temporiamente indisponível");
    }
}

package com.senai.nprrs_tagdog_backend.domain.exceptions;

public class DispositivoInativoException extends DominioException {
    public DispositivoInativoException( ) {
        super("Dispositivo de rastreamento inativo");
    }
}

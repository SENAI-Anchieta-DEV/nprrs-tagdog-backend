package com.senai.nprrs_tagdog_backend.domain.exceptions;

public class GeofenceException extends DominioException {
    public GeofenceException() {
        super("Pet fora da área permitida.");
    }
}

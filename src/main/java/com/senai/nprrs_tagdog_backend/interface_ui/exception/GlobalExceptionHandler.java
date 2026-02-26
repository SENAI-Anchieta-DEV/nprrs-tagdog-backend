package com.senai.nprrs_tagdog_backend.interface_ui.exception;

import com.senai.nprrs_tagdog_backend.domain.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail handleNotFound(
            EntidadeNaoEncontradaException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.build(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    // 400
    @ExceptionHandler({
            DadosInvalidosException.class,
            RegraNegocioException.class
    })
    public ProblemDetail handleBadRequest(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.build(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    // 403
    @ExceptionHandler(AcessoNegadoException.class)
    public ProblemDetail handleForbidden(
            AcessoNegadoException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.build(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request
        );
    }

    // 409
    @ExceptionHandler({
            EntidadeDuplicadaException.class,
            ConflitosDeEstadoException.class,
            OperacaoNaoPermitidaException.class
    })
    public ProblemDetail handleConflict(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.build(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    // 503
    @ExceptionHandler(RecursoIndisponivelException.class)
    public ProblemDetail handleServiceUnavailable(
            RecursoIndisponivelException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.build(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage(),
                request
        );
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado.",
                request
        );
    }
}
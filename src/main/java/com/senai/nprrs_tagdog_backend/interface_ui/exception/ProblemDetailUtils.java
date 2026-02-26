package com.senai.nprrs_tagdog_backend.interface_ui.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;

public class ProblemDetailUtils {

    private ProblemDetailUtils() {}

    public static ProblemDetail build(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(status, message);

        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("path", request.getRequestURI());

        return problemDetail;
    }
}
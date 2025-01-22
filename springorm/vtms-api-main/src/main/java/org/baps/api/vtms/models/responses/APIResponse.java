package org.baps.api.vtms.models.responses;

import org.springframework.http.HttpStatus;

public record APIResponse(HttpStatus status, int code, String message, Object result) {

    public APIResponse(final HttpStatus status, final String message, final Object result) {
        this(status, status.value(), message, result);
    }

}

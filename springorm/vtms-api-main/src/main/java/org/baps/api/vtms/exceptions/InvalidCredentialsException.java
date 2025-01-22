package org.baps.api.vtms.exceptions;

import java.io.Serial;

public class InvalidCredentialsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7445844769428291009L;

    public InvalidCredentialsException() {
        super("Invalid credentials");
    }

}

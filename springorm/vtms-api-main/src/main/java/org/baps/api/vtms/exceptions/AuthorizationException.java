package org.baps.api.vtms.exceptions;

import java.io.Serial;

public class AuthorizationException extends RuntimeException {

    private static final String ERR_MSG = "Access Denied";

    @Serial
    private static final long serialVersionUID = -1L;

    public AuthorizationException() {
        super(ERR_MSG);
    }

}

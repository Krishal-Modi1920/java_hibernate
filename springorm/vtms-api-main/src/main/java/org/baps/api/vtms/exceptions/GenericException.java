package org.baps.api.vtms.exceptions;

import java.io.Serial;

public class GenericException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5080130800384801463L;

    public GenericException(final String message) {
        super(message);
    }

}

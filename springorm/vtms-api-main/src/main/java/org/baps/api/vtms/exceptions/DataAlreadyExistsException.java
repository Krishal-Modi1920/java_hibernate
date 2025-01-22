package org.baps.api.vtms.exceptions;

import java.io.Serial;

public class DataAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -48310101126659672L;

    public DataAlreadyExistsException(final String message) {
        super(message);
    }
}

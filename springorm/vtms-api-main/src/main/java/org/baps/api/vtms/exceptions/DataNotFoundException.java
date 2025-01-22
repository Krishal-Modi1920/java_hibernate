package org.baps.api.vtms.exceptions;

import java.io.Serial;

public class DataNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1320091007291905878L;

    public DataNotFoundException(final String message) {
        super(message);
    }
}

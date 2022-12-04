package io.github.ss3rg3.elsa.exceptions;

import java.io.IOException;

public class ElsaIOException extends ElsaException {

    public ElsaIOException(final IOException e) {
        super(e);
    }

}

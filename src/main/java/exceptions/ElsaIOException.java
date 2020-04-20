package exceptions;

import java.io.IOException;

public class ElsaIOException extends ElsaException {

    public ElsaIOException(final IOException e) {
        super(e);
    }

}

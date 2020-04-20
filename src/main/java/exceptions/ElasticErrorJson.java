package exceptions;

import responses.ExceptionResponse.Error;

import java.util.List;

public class ElasticErrorJson {
    private String type;
    private String reason;
    private List<Error.Cause> root_cause;

    public static class Cause {
        private final String type;
        private final String reason;

        public Cause(final String type, final String reason) {
            this.type = type;
            this.reason = reason;
        }

        public String getType() {
            return this.type;
        }

        public String getReason() {
            return this.reason;
        }

    }

    public String getType() {
        return this.type;
    }

    public String getReason() {
        return this.reason;
    }

    public List<Error.Cause> getCauses() {
        return this.root_cause;
    }

}

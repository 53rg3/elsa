/*
 * Copyright 2018 Sergej Schaefer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package responses;

import responses.ExceptionResponse.ErrorHolder.Cause;
import statics.ElsaStatics;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

public class ExceptionResponse {

    // ------------------------------------------------------------------------------------------ //
    // CONFIGURATOR
    // ------------------------------------------------------------------------------------------ //

    @FunctionalInterface
    public interface Configurator {

        default Config loadDefaults() {
            return new Config();
        }

        default void validate(final Config config) {
            Objects.requireNonNull(config.status, "Status must not be NULL.");
            Objects.requireNonNull(config.error, "Error must not be NULL.");
        }

        default Config applyCustomConfig(final Configurator configurator) {
            final Config config = configurator.loadDefaults();
            configurator.configure(config);
            configurator.validate(config);
            return config;
        }

        void configure(Config config);
    }


    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //

    private final Integer status;
    private final ErrorHolder error;


    // ------------------------------------------------------------------------------------------ //
    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------ //

    private ExceptionResponse(final Configurator configurator) {
        final Config config = configurator.applyCustomConfig(configurator);
        this.status = config.status;
        this.error = config.error;
    }

    public static ExceptionResponse createFromThrowable(final Throwable throwable) {
        final Matcher matcher = ElsaStatics.jsonExtractorPattern.matcher(throwable.getMessage());
        if (matcher.find()) {
            return ElsaStatics.GSON.fromJson(matcher.group(0), ExceptionResponse.class);
        } else {
            return new ExceptionResponse(c -> c
                    .status(-1)
                    .errorHolder("Could not extract ResponseException.", "Could not extract ResponseException."));
        }
    }

    public static ExceptionResponse createConnectionRefused() {
        return new ExceptionResponse(c->c
                .status(503)
                .errorHolder("ConnectException", "Connection refused. Either cluster is offline or it refused your request."));
    }

    public static ExceptionResponse createUncategorizedCause(Exception e) {
        return new ExceptionResponse(c->c
                .status(-1)
                .errorHolder(e.getClass().toString(), e.getMessage()+" - Check logs at "+new Date()));
    }


    // ------------------------------------------------------------------------------------------ //
    // CONFIG
    // ------------------------------------------------------------------------------------------ //

    public static class Config {
        private Config() {
        }

        private Integer status;
        private ErrorHolder error;

        public Config status(final Integer defaultIsPlaceholder) {
            this.status = defaultIsPlaceholder;
            return this;
        }

        public Config errorHolder(final String type, final String reason) {
            final ErrorHolder errorHolder = new ErrorHolder();
            errorHolder.type = type;
            errorHolder.reason = reason;
            errorHolder.root_cause = this.createSingleCause(type, reason);
            this.error = errorHolder;
            return this;
        }

        private List<Cause> createSingleCause(final String type, final String reason) {
            return Arrays.asList(new Cause(type, reason));

        }

    }

    public static class ErrorHolder {
        private String type;
        private String reason;
        private List<Cause> root_cause;

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

        public List<Cause> getCauses() {
            return this.root_cause;
        }

    }

    public Integer getStatus() {
        return this.status;
    }

    public ErrorHolder getError() {
        return this.error;
    }
}

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

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ElsaResponse<T> {

    private final T response;
    private final ExceptionResponse exceptionResponse;
    private static final ElsaResponse<?> EMPTY = new ElsaResponse<>();

    private static final String NO_RESULT = "No response present, i.e. the request was successful, " +
            "but returned no result. Check with .isPresent() first.";
    private static final String HAS_EXCEPTION = "Can't get response, because it was returned with an exception. " +
            "Check with .hasException() first. You can also access it via .getExceptionResponse().";


    private ElsaResponse() {
        this.response = null;
        this.exceptionResponse = null;
    }

    private ElsaResponse(final T response) {
        this.response = Objects.requireNonNull(response);
        this.exceptionResponse = null;
    }

    private ElsaResponse(final Exception e) {
        this.response = null;
        this.exceptionResponse = ExceptionExtractor.createErrorResponse(e);
    }

    private ElsaResponse(final ExceptionResponse exceptionResponse) {
        this.response = null;
        this.exceptionResponse = exceptionResponse;
    }

    public static <T> ElsaResponse<T> of(final T value) {
        return new ElsaResponse<>(value);
    }

    public static <T extends JsonConvertable> ElsaResponse<T> of(final T value) {
        if (!value.validate()) {
            throw new IllegalArgumentException("Validation failed for response.");
        }
        return new ElsaResponse<>(value);
    }

    public static <T> ElsaResponse<T> of(final Exception e) {
        return new ElsaResponse<>(e);
    }

    public static <T> ElsaResponse<T> of(final ExceptionResponse exceptionResponse) {
        return new ElsaResponse<>(exceptionResponse);
    }

    public static <T> ElsaResponse<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    private static <T> ElsaResponse<T> empty() {
        @SuppressWarnings("unchecked")
        ElsaResponse<T> t = (ElsaResponse<T>) EMPTY;
        return t;
    }

    public T get() {
        if (exceptionResponse != null) {
            throw new NoSuchElementException(HAS_EXCEPTION);
        }
        if (response == null) {
            throw new NoSuchElementException(NO_RESULT);
        }
        return response;
    }

    public boolean isPresent() {
        return response != null;
    }

    public boolean hasException() {
        return exceptionResponse != null;
    }

    public void ifPresent(final Consumer<? super T> consumer) {
        if (response != null)
            consumer.accept(response);
    }

    public void ifException(final Consumer<ExceptionResponse> consumer) {
        if (exceptionResponse != null)
            consumer.accept(exceptionResponse);
    }

    public ElsaResponse<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent())
            return this;
        else
            return predicate.test(response) ? this : empty();
    }

    public <U> ElsaResponse<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return ElsaResponse.ofNullable(mapper.apply(response));
        }
    }

    public <U> ElsaResponse<U> flatMap(Function<? super T, ElsaResponse<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return Objects.requireNonNull(mapper.apply(response));
        }
    }

    public T orElse(final T other) {
        return response != null ? response : other;
    }

    public T orElseGet(final Supplier<? extends T> other) {
        return response != null ? response : other.get();
    }

    public <X extends Throwable> T orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
        if (response != null) {
            return response;
        } else {
            throw exceptionSupplier.get();
        }
    }

    public ExceptionResponse getExceptionResponse() {
        return this.exceptionResponse;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ElsaResponse)) {
            return false;
        }

        final ElsaResponse<?> other = (ElsaResponse<?>) obj;
        return Objects.equals(response, other.response);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(response);
    }

    @Override
    public String toString() {
        return response != null
                ? String.format("ElsaResponse[%s]", response)
                : "ElsaResponse.empty";
    }

}

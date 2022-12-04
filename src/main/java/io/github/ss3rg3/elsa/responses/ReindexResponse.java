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

package io.github.ss3rg3.elsa.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class ReindexResponse implements JsonConvertible {

    @Override
    public boolean validate() {
        Objects.requireNonNull(this.took, "'took' must not be NULL.");
        Objects.requireNonNull(this.timedOut, "'timedOut' must not be NULL.");
        Objects.requireNonNull(this.total, "'total' must not be NULL.");
        Objects.requireNonNull(this.updated, "'updated' must not be NULL.");
        Objects.requireNonNull(this.created, "'created' must not be NULL.");
        Objects.requireNonNull(this.deleted, "'deleted' must not be NULL.");
        Objects.requireNonNull(this.batches, "'batches' must not be NULL.");
        Objects.requireNonNull(this.versionConflicts, "'versionConflicts' must not be NULL.");
        Objects.requireNonNull(this.noops, "'noops' must not be NULL.");
        Objects.requireNonNull(this.retries, "'retries' must not be NULL.");
        Objects.requireNonNull(this.throttledMillis, "'throttledMillis' must not be NULL.");
        Objects.requireNonNull(this.requestsPerSecond, "'requestsPerSecond' must not be NULL.");
        Objects.requireNonNull(this.throttledUntilMillis, "'throttledUntilMillis' must not be NULL.");
        Objects.requireNonNull(this.failures, "'failures' must not be NULL.");
        return true;
    }

    @SerializedName("took")
    @Expose
    private Integer took;

    @SerializedName("timed_out")
    @Expose
    private Boolean timedOut;

    @SerializedName("total")
    @Expose
    private Integer total;

    @SerializedName("updated")
    @Expose
    private Integer updated;

    @SerializedName("created")
    @Expose
    private Integer created;

    @SerializedName("deleted")
    @Expose
    private Integer deleted;

    @SerializedName("batches")
    @Expose
    private Integer batches;

    @SerializedName("version_conflicts")
    @Expose
    private Integer versionConflicts;

    @SerializedName("noops")
    @Expose
    private Integer noops;

    @SerializedName("retries")
    @Expose
    private Retries retries;

    @SerializedName("throttled_millis")
    @Expose
    private Integer throttledMillis;

    @SerializedName("requests_per_second")
    @Expose
    private Double requestsPerSecond;

    @SerializedName("throttled_until_millis")
    @Expose
    private Integer throttledUntilMillis;

    @SerializedName("failures")
    @Expose
    private List<Object> failures;


    public static class Retries {

        @SerializedName("bulk")
        @Expose
        private Integer bulk;

        @SerializedName("search")
        @Expose
        private Integer search;

        public Integer getBulk() {
            return this.bulk;
        }

        public Integer getSearch() {
            return this.search;
        }
    }

    public Integer getTook() {
        return this.took;
    }

    public Boolean getTimedOut() {
        return this.timedOut;
    }

    public Integer getTotal() {
        return this.total;
    }

    public Integer getUpdated() {
        return this.updated;
    }

    public Integer getCreated() {
        return this.created;
    }

    public Integer getDeleted() {
        return this.deleted;
    }

    public Integer getBatches() {
        return this.batches;
    }

    public Integer getVersionConflicts() {
        return this.versionConflicts;
    }

    public Integer getNoops() {
        return this.noops;
    }

    public Retries getRetries() {
        return this.retries;
    }

    public Integer getThrottledMillis() {
        return this.throttledMillis;
    }

    public Double getRequestsPerSecond() {
        return this.requestsPerSecond;
    }

    public Integer getThrottledUntilMillis() {
        return this.throttledUntilMillis;
    }

    public List<Object> getFailures() {
        return this.failures;
    }
}

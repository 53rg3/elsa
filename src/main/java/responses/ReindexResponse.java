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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class ReindexResponse implements JsonConvertable {

    @Override
    public boolean validate() {
        Objects.requireNonNull(took, "'took' must not be NULL.");
        Objects.requireNonNull(timedOut, "'timedOut' must not be NULL.");
        Objects.requireNonNull(total, "'total' must not be NULL.");
        Objects.requireNonNull(updated, "'updated' must not be NULL.");
        Objects.requireNonNull(created, "'created' must not be NULL.");
        Objects.requireNonNull(deleted, "'deleted' must not be NULL.");
        Objects.requireNonNull(batches, "'batches' must not be NULL.");
        Objects.requireNonNull(versionConflicts, "'versionConflicts' must not be NULL.");
        Objects.requireNonNull(noops, "'noops' must not be NULL.");
        Objects.requireNonNull(retries, "'retries' must not be NULL.");
        Objects.requireNonNull(throttledMillis, "'throttledMillis' must not be NULL.");
        Objects.requireNonNull(requestsPerSecond, "'requestsPerSecond' must not be NULL.");
        Objects.requireNonNull(throttledUntilMillis, "'throttledUntilMillis' must not be NULL.");
        Objects.requireNonNull(failures, "'failures' must not be NULL.");
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



    public class Retries {

        @SerializedName("bulk")
        @Expose
        private Integer bulk;

        @SerializedName("search")
        @Expose
        private Integer search;

        public Integer getBulk() {
            return bulk;
        }

        public Integer getSearch() {
            return search;
        }
    }

    public Integer getTook() {
        return took;
    }

    public Boolean getTimedOut() {
        return timedOut;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getUpdated() {
        return updated;
    }

    public Integer getCreated() {
        return created;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public Integer getBatches() {
        return batches;
    }

    public Integer getVersionConflicts() {
        return versionConflicts;
    }

    public Integer getNoops() {
        return noops;
    }

    public Retries getRetries() {
        return retries;
    }

    public Integer getThrottledMillis() {
        return throttledMillis;
    }

    public Double getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public Integer getThrottledUntilMillis() {
        return throttledUntilMillis;
    }

    public List<Object> getFailures() {
        return failures;
    }
}

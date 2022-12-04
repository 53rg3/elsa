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

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SnapshotInfoResponse implements JsonConvertible {
    private SnapshotInfoResponse() {
    }

    @Override
    public boolean validate() {
        Objects.requireNonNull(this.name, "'name' must not be NULL.");
        Objects.requireNonNull(this.uuid, "'uuid' must not be NULL.");
        Objects.requireNonNull(this.versionId, "'versionId' must not be NULL.");
        Objects.requireNonNull(this.version, "'version' must not be NULL.");
        Objects.requireNonNull(this.indices, "'indices' must not be NULL.");
        Objects.requireNonNull(this.includeGlobalState, "'includeGlobalState' must not be NULL.");
        Objects.requireNonNull(this.state, "'state' must not be NULL.");
        Objects.requireNonNull(this.startTime, "'startTime' must not be NULL.");
        Objects.requireNonNull(this.startTimeInMillis, "'startTimeInMillis' must not be NULL.");
        Objects.requireNonNull(this.endTime, "'endTime' must not be NULL.");
        Objects.requireNonNull(this.endTimeInMillis, "'endTimeInMillis' must not be NULL.");
        Objects.requireNonNull(this.durationInMillis, "'durationInMillis' must not be NULL.");
        Objects.requireNonNull(this.failures, "'failures' must not be NULL.");
        Objects.requireNonNull(this.shards, "'shards' must not be NULL.");

        return true;
    }


    @SerializedName("snapshot")
    @Expose
    private String name;

    @SerializedName("uuid")
    @Expose
    private String uuid;

    @SerializedName("version_id")
    @Expose
    private Long versionId;

    @SerializedName("version")
    @Expose
    private String version;

    @SerializedName("indices")
    @Expose
    private List<String> indices;

    @SerializedName("include_global_state")
    @Expose
    private Boolean includeGlobalState;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("start_time")
    @Expose
    private Date startTime;

    @SerializedName("start_time_in_millis")
    @Expose
    private Long startTimeInMillis;

    @SerializedName("end_time")
    @Expose
    private Date endTime;

    @SerializedName("end_time_in_millis")
    @Expose
    private Long endTimeInMillis;

    @SerializedName("duration_in_millis")
    @Expose
    private Long durationInMillis;

    @SerializedName("failures")
    @Expose
    private List<String> failures;

    @SerializedName("shards")
    @Expose
    private Shards shards;

    public static class Shards {

        @SerializedName("total")
        @Expose
        private Integer total;

        @SerializedName("failed")
        @Expose
        private Integer failed;

        @SerializedName("successful")
        @Expose
        private Integer successful;

        public Integer getTotal() {
            return this.total;
        }

        public Integer getFailed() {
            return this.failed;
        }

        public Integer getSuccessful() {
            return this.successful;
        }
    }

    public String getName() {
        return this.name;
    }

    public String getUuid() {
        return this.uuid;
    }

    public Long getVersionId() {
        return this.versionId;
    }

    public String getVersion() {
        return this.version;
    }

    public List<String> getIndices() {
        return this.indices;
    }

    public boolean getIncludeGlobalState() {
        return this.includeGlobalState;
    }

    public String getState() {
        return this.state;
    }

    public Long getStartTimeInMillis() {
        return this.startTimeInMillis;
    }

    public Long getEndTimeInMillis() {
        return this.endTimeInMillis;
    }

    public Long getDurationInMillis() {
        return this.durationInMillis;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public List<String> getFailures() {
        return this.failures;
    }

    public Shards getShards() {
        return this.shards;
    }
}

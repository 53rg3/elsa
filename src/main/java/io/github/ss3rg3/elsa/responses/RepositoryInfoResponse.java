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

import java.util.Objects;

public class RepositoryInfoResponse implements JsonConvertible {

    @Override
    public boolean validate() {
        Objects.requireNonNull(this.name, "'name' must not be NULL.");
        Objects.requireNonNull(this.type, "'type' must not be NULL.");
        Objects.requireNonNull(this.settings, "'settings' must not be NULL.");
        return true;
    }

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("settings")
    @Expose
    private Settings settings;

    public static class Settings {

        @SerializedName("compress")
        @Expose
        private boolean compress;

        @SerializedName("location")
        @Expose
        private String location;

        public boolean getCompress() {
            return this.compress;
        }

        public String getLocation() {
            return this.location;
        }
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public Settings getSettings() {
        return this.settings;
    }
}

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

public class ConfirmationResponse implements JsonConvertible {

    private Boolean acknowledged;
    private Boolean accepted;

    /** Use ResponseFactory */
    private ConfirmationResponse() {}

    public Boolean hasSucceeded() {
        if (this.acknowledged != null) {
            return this.acknowledged;
        }
        if (this.accepted != null) {
            return this.accepted;
        }
        return false;
    }

    @Override
    public boolean validate() {
        return acknowledged != null || accepted != null;
    }
}

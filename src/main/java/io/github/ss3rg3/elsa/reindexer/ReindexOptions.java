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

package io.github.ss3rg3.elsa.reindexer;

public enum ReindexOptions {

    USE_THE_INNER_CLASSES;


    public enum ReindexMode {
        /** Attempts to create the index & mappings with the io.github.ss3rg3.elsa.model provided in destination settings. Throws if no io.github.ss3rg3.elsa.model class was provided. */
        CREATE_NEW_INDEX_FROM_MODEL_IN_DESTINATION,

        /** Attempts to update the mappings with the io.github.ss3rg3.elsa.model provided in destination settings.
         * Index must already exist and have compatible mappings for updating, or non at all. Throws if no io.github.ss3rg3.elsa.model class was provided. */
        ABORT_IF_MAPPING_INCORRECT,

        /** No attempt to create an index or update its mappings. Index creation must have been done before the operation. */
        DESTINATION_INDEX_AND_MAPPINGS_ALREADY_EXIST
    }


    /**
     * Determines the type of the reindexing operation when encountering existing documents
     */
    public enum OpType {
        /** Will cause _reindex to only create missing documents in the target index. All existing documents will cause a version conflict.
         * Conflicts will case the operation to abort. */
        CREATE("create");

        private final String value;
        OpType(String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return this.value;
        }
    }

    /**
     * Used in combination with op_type to configure how conflicts are handled
     */
    public enum Conflicts {
        /** Proceed when conflicts and just count them*/
        PROCEED("proceed");

        private final String value;
        Conflicts(String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return this.value;
        }
    }

    /** How to handle versions of existing documents. You can either dump documents blindly into the index,
     * overwriting any that happen to have the same type and id, or preserve the version from the script,
     * create any documents that are missing, and update any documents that have an older version in the
     * destination index than they do in the script index
     */
    public enum VersionType {

        /** Dumps documents into the target, overwriting any that happen to have the same type and id */
        INTERNAL("internal"),
        /** Preserves the version from the script, create any documents that are missing, and update
         * any documents that have an older version in the destination index than they do in the
         * script index */
        EXTERNAL("external");

        private final String value;
        VersionType(String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return this.value;
        }
    }

    public enum ScriptingLanguage {
        PAINLESS("painless");

        private final String value;
        ScriptingLanguage(String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return this.value;
        }
    }

}

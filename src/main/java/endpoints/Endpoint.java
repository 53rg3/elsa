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

package endpoints;

public final class Endpoint {

    // ------------------------------------------------------------------------------------------ //
    // REINDEX
    // ------------------------------------------------------------------------------------------ //
    public static final String REINDEX = "_reindex";


    // ------------------------------------------------------------------------------------------ //
    // MAPPINGS
    // ------------------------------------------------------------------------------------------ //

    public static final class INDEX_MAPPING {
        private INDEX_MAPPING() {
        }

        private static final String ROOT = "_mappings";

        public static String update(final String indexName) {
            return indexName + "/" + ROOT;
        }
    }


    // ------------------------------------------------------------------------------------------ //
    // SNAPSHOT
    // ------------------------------------------------------------------------------------------ //

    public static final class SNAPSHOT {
        private SNAPSHOT() {
        }

        private static final String ROOT = "_snapshot";


        public static final class INFO {
            private INFO() {
            }

            public static String getRepositories() {
                return ROOT + "/_all";
            }

            public static String getRepositoryByName(final String repositoryName) {
                return ROOT + "/" + repositoryName;
            }

            public static String getSnapshotByName(final String repositoryName, final String snapshotName) {
                return ROOT + "/" + repositoryName + "/" + snapshotName;
            }

            public static String getSnapshots(final String repository) {
                return ROOT + "/" + repository + "/_all";
            }
        }


        public static final class CREATE {
            private CREATE() {
            }

            public static String createRepository(final String repositoryName) {
                return ROOT + "/" + repositoryName;
            }

            public static String createSnapshot(final String repositoryName, final String snapshotName) {
                return ROOT + "/" + repositoryName + "/" + snapshotName;
            }
        }


        public static final class RESTORE {
            private RESTORE() {
            }

            public static String restoreSnapshot(final String repositoryName, final String snapshotName) {
                return ROOT + "/" + repositoryName + "/" + snapshotName + "/_restore";
            }
        }


        public static final class DELETE {
            private DELETE() {
            }

            public static String deleteSnapshot(final String repositoryName, final String snapshotName) {
                return ROOT + "/" + repositoryName + "/" + snapshotName;
            }

            public static String deleteRepository(final String repositoryName) {
                return ROOT + "/" + repositoryName;
            }
        }
    }

}

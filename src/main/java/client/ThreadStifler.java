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

package client;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ThreadStifler {

    private static final int WAIT_TILL_RECONNECT_IN_MS = 1000;
    private static final Logger logger = LoggerFactory.getLogger(ThreadStifler.class);

    protected static void waitTillClusterIsOnline(final boolean shouldProceed, final RestHighLevelClient client) {

        if (!shouldProceed) {
            return;
        }

        while (true) {
            if (isClusterOnline(client)) {
                logger.warn("Yay, cluster is online.");
                break;
            } else {
                logger.warn("Cluster is offline, reconnecting in " + WAIT_TILL_RECONNECT_IN_MS + "ms.");
                sleep();
            }
        }
    }

    private static boolean isClusterOnline(final RestHighLevelClient client) {
        try {
            return client.ping();
        } catch (final IOException e) {
            // NO OP
        }
        return false;
    }

    private static void sleep() {
        try {
            Thread.sleep(WAIT_TILL_RECONNECT_IN_MS);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}

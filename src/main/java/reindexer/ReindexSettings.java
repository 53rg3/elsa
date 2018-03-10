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

package reindexer;

import helpers.XJson;
import model.ElsaModel;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reindexer.ReindexOptions.Conflicts;

import java.io.IOException;
import java.util.Map;

public class ReindexSettings {

    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //

    private static final Logger logger = LoggerFactory.getLogger(ReindexSettings.class);
    private final XContentBuilder xContentBuilder;
    private final Class<? extends ElsaModel> modelClass;

    
    // ------------------------------------------------------------------------------------------ //
    // BUILD
    // ------------------------------------------------------------------------------------------ //

    /** Use the builder */
    private ReindexSettings(final ReindexSettingsBuilder builder) {
        this.xContentBuilder = this.createXContentBuilder(builder.xJson.toMap());
        this.modelClass = builder.modelClass;
    }

    private XContentBuilder createXContentBuilder(final Map<String,Object> map) {
        try {
            return XContentFactory.jsonBuilder().value(map);
        } catch (final IOException e) {
            logger.error("", e);
        }
        throw new IllegalStateException("Couldn't create XContentBuilder.");
    }
    
    
    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //
    
    public static class ReindexSettingsBuilder {
        private final XJson xJson = new XJson();
        private Class<? extends ElsaModel> modelClass;

        /** (Optional) Proceed when conflict occur, just count them. */
        public ReindexSettingsBuilder conflicts(final Conflicts optionalSetting) {
            this.xJson.field("conflicts", optionalSetting.toString());
            return this;
        }

        /** (Optional) Limit the total amount of documents which shall be transferred */
        public ReindexSettingsBuilder totalSize(final Integer optionalSetting) {
            this.xJson.field("size", optionalSetting);
            return this;
        }

        /** (Mandatory) Configuration for the source index which shall be reindexed */
        public ReindexSettingsBuilder configureSource(final ReindexSource.Configurator configurator) {
            this.xJson.field("source", configurator.applyCustomConfig(configurator).toMap());
            return this;
        }

        /** (Mandatory) Configuration for the target index which shall be filled */
        public ReindexSettingsBuilder configureDestination(final ReindexDestination.Configurator configurator) {
            ReindexDestination reindexDestination = configurator.applyCustomConfig(configurator);
            this.xJson.field("dest", reindexDestination.getXJson().toMap());
            this.modelClass = reindexDestination.getModelClass();
            return this;
        }

        /** (Optional) Script to apply to the reindexation process */
        public ReindexSettingsBuilder configureScript(final ReindexScript.Configurator configurator) {
            this.xJson.field("script", configurator.applyCustomConfig(configurator).toMap());
            return this;
        }

        public ReindexSettings build() {
            xJson.throwIfFieldNotExists("source", "Settings for 'source' must not be NULL.");
            xJson.throwIfFieldNotExists("dest", "Settings for 'destination' must not be NULL.");

            return new ReindexSettings(this);
        }
    }

    public XContentBuilder getXContentBuilder() {
        return this.xContentBuilder;
    }

    public Class<? extends ElsaModel> getModelClass() {
        return modelClass;
    }
}

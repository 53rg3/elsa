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

package helpers;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class Search {

//    @FunctionalInterface
//    public interface SearchRequestConfigurator {
//
//        default SearchRequest applyCustomConfig(SearchRequestConfigurator searchRequestConfigurator) {
//            SearchRequest searchRequest = new SearchRequest();
//            searchRequestConfigurator.configure(searchRequest);
//            return searchRequest;
//        }
//
//        void configure(SearchRequest searchRequest);
//    }
//
//    @FunctionalInterface
//    public interface SearchSourceConfigurator {
//
//        default SearchSourceBuilder applyCustomConfig(SearchSourceConfigurator searchSourceConfigurator) {
//            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//            searchSourceConfigurator.configure(searchSourceBuilder);
//            return searchSourceBuilder;
//        }
//
//        void configure(SearchSourceBuilder searchSourceBuilder);
//    }
//
//    public static SearchRequest req(SearchRequestConfigurator useFunctionLambda) {
//        return useFunctionLambda.applyCustomConfig(useFunctionLambda);
//    }
//
//    public static SearchSourceBuilder src(SearchSourceConfigurator useFunctionLambda) {
//        return useFunctionLambda.applyCustomConfig(useFunctionLambda);
//    }

    public static SearchRequest req() {
        return new SearchRequest();
    }

    public static SearchSourceBuilder src() {
        return new SearchSourceBuilder();
    }

}

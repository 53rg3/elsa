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

package assets;

import client.ElsaClient;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;

public class AsyncDeleteListener implements ActionListener<DeleteResponse> {

    private final Check check;
    private final ElsaClient elsaClient;

    public AsyncDeleteListener(Check check, ElsaClient elsaClient) {
        this.check = check;
        this.elsaClient = elsaClient;
    }

    @Override
    public void onResponse(DeleteResponse deleteResponse) {
        this.check.setWasSuccessful(true);
    }

    @Override
    public void onFailure(Exception e) {

    }
}

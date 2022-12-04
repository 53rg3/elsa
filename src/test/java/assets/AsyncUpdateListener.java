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

import io.github.ss3rg3.elsa.ElsaClient;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.update.UpdateResponse;

public class AsyncUpdateListener implements ActionListener<UpdateResponse> {

    private final Check check;
    private final ElsaClient elsaClient;

    public AsyncUpdateListener(Check check, ElsaClient elsaClient) {
        this.check = check;
        this.elsaClient = elsaClient;
    }

    @Override
    public void onResponse(UpdateResponse updateResponse) {
        this.check.setWasSuccessful(true);
    }

    @Override
    public void onFailure(Exception e) {

    }
}

/*
 *   Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.choreo.analytics.api.impl;

import org.springframework.stereotype.Component;
import org.wso2.choreo.analytics.api.gql.API;
import org.wso2.choreo.analytics.api.gql.DataFetchersDelegateAPI;

import java.util.Collections;
import java.util.List;

@Component
public class APIDataFetcher implements DataFetchersDelegateAPI {

    @Override
    public List<API> batchLoader(List<String> keys) {
        return Collections.emptyList();
    }
}

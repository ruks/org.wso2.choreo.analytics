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

package org.wso2.choreo.analytics.gql.kusto;

import com.microsoft.azure.kusto.data.ClientImpl;
import com.microsoft.azure.kusto.data.ClientRequestProperties;
import com.microsoft.azure.kusto.data.ConnectionStringBuilder;
import com.microsoft.azure.kusto.data.KustoOperationResult;
import com.microsoft.azure.kusto.data.KustoResultColumn;
import com.microsoft.azure.kusto.data.exceptions.DataClientException;
import com.microsoft.azure.kusto.data.exceptions.DataServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.choreo.analytics.gql.config.ConfigHolder;
import org.wso2.choreo.analytics.gql.config.Kusto;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KustoQueryClient {
    private static final Logger log = LoggerFactory.getLogger(KustoQueryClient.class);
    private ClientImpl client;
    private String dbName;
    private ClientRequestProperties clientRequestProperties;

    private static KustoQueryClient inst;

    public static KustoQueryClient getInstance() {
        if(inst == null) {
            inst = new KustoQueryClient();
            inst.init();
        }
        return inst;
    }

    private void init() {
        Kusto kusto = ConfigHolder.getInstance().getConfiguration().getKusto();
        String ClientID = kusto.getClientid();
        String pass = kusto.getPass();
        String auth = kusto.getAuth();
        dbName = kusto.getDatabase();
        String connectionUrl = kusto.getUrl();

        ConnectionStringBuilder csb =
                ConnectionStringBuilder.createWithAadApplicationCredentials(connectionUrl, ClientID, pass, auth);

        try {
            client = new ClientImpl(csb);
        } catch (URISyntaxException e) {
            log.error("Error initializing kusto client", e);
        }
        clientRequestProperties = new ClientRequestProperties();
        clientRequestProperties.setOption("ClientRequestId", "ohadId");
        clientRequestProperties.setTimeoutInMilliSec(999999L);
    }

    private ClientImpl getClient() {
        if (client == null) {
            init();
        }
        return client;
    }

    public List<Map<String, Object>> execute(String query) throws QueryException {
        ClientImpl client = getClient();
        KustoOperationResult results;
        try {
            log.debug("executing kusto query: " + query);
            long startTime = System.currentTimeMillis();
            results = client.execute(dbName, query, clientRequestProperties);
            log.info("Time take for the query {} is {}ms ", query, System.currentTimeMillis() - startTime);
        } catch (DataServiceException | DataClientException e) {
            throw new QueryException("Error occurred while queries data.", e);
        }
        KustoResultColumn[] columns = results.getPrimaryResults().getColumns();
        ArrayList<ArrayList<Object>> rows = results.getPrimaryResults().getData();
        List<Map<String, Object>> list = new ArrayList<>();
        for (ArrayList<Object> aRow : rows) {
            Map<String, Object> aMap = new HashMap<>();
            for (int i = 0; i < columns.length; i++) {
                aMap.put(columns[i].getColumnName(), aRow.get(i));
            }
            list.add(aMap);
        }
        return list;
    }

}

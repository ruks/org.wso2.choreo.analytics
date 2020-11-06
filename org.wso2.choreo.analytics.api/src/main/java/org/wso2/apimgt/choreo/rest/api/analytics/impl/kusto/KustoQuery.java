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

package org.wso2.apimgt.choreo.rest.api.analytics.impl.kusto;

import com.microsoft.azure.kusto.data.ClientImpl;
import com.microsoft.azure.kusto.data.ClientRequestProperties;
import com.microsoft.azure.kusto.data.ConnectionStringBuilder;
import com.microsoft.azure.kusto.data.KustoOperationResult;
import com.microsoft.azure.kusto.data.KustoResultColumn;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.config.ConfigHolder;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.config.Kusto;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KustoQuery {
    private ClientImpl client;
    private String dbName;
    private ClientRequestProperties clientRequestProperties;

    private void init() {
        Kusto kusto = ConfigHolder.getInstance().getConfiguration().getKusto();
        String ClientID = kusto.getClientid();
        String pass = kusto.getPass();
        String auth = kusto.getAuth();
        dbName = kusto.getDatabase();
        String connectionUrl = kusto.getUrl();

        ConnectionStringBuilder csb = ConnectionStringBuilder
                .createWithAadApplicationCredentials(connectionUrl, ClientID, pass,
                        auth);

        try {
            client = new ClientImpl(csb);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        clientRequestProperties = new ClientRequestProperties();
        clientRequestProperties.setOption("ClientRequestId", "ohadId");
        clientRequestProperties.setTimeoutInMilliSec(999999L);
    }

    private ClientImpl getClient() {
        if(client == null) {
            init();
        }
        return client;
    }

    public List<Map<String, Object>> getLatencyData(String from, String to, int limit, String orderBy, boolean asc,
            String tenant, String orgID, List<String> selects, List<String> filters, List<String> groupBy) {
        try {

            ClientImpl client = getClient();
            String query = "analytics_poc_pipeline_latency_test\n"
                    + "| where AGG_WINDOW_START_TIME > datetime(" + from + ") and AGG_WINDOW_START_TIME < "
                    + "datetime(" + to + ")\n"
                    + "| where apiCreatorTenantDomain == '" + tenant + "'"
                    + (filters.size() > 0 ? " and " + String.join(" and ", filters) : "" ) + "\n"
                    + "| summarize max(avgResponseLatency), max(avgServiceLatency), max(avgBackendLatency), max"
                    + "(avgRequestMediationLatency), max(avgResponseMediationLatency), max(avgSecurityLatency), max"
                    + "(avgThrottlingLatency), max(avgOtherLatency) by "
                    + (groupBy.size() > 0 ? String.join(",", groupBy) : "" ) + "\n"
                    + "| project-rename avgResponseLatency = max_avgResponseLatency,  avgServiceLatency = "
                    + "max_avgServiceLatency, avgBackendLatency=max_avgBackendLatency, "
                    + "avgRequestMediationLatency=max_avgRequestMediationLatency,  "
                    + "avgResponseMediationLatency=max_avgResponseMediationLatency, "
                    + "avgSecurityLatency=max_avgSecurityLatency, avgThrottlingLatency=max_avgThrottlingLatency, "
                    + "avgOtherLatency=max_avgOtherLatency" + "\n"
                    + "| project " + String.join(",", selects) + "\n"
                    + "| top " + limit+ " by " + orderBy + " " + (asc ? "asc" : "desc");
            System.out.println(query);
            KustoOperationResult results = client.execute(dbName, query, clientRequestProperties);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
}

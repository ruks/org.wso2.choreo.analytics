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

package org.wso2.choreo.analytics.api.kusto;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wso2.choreo.analytics.api.gql.ErrorSummary;
import org.wso2.choreo.analytics.api.gql.LatencySummary;
import org.wso2.choreo.analytics.api.gql.SuccessSummary;
import org.wso2.choreo.analytics.api.gql.TimeFilter;
import org.wso2.choreo.analytics.api.security.JWTUserDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OverviewQueryExecutor {
    private static final Logger log = LoggerFactory.getLogger(UtilQueryExecutor.class);

    private final String QUERY_TOTAL_TRAFFIC = "analytics_request_summary\n"
            + "| where AGG_WINDOW_START_TIME > datetime(${from}) and AGG_WINDOW_START_TIME < datetime"
            + "(${to})"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerID == \"${customerId}\""
            + "| summarize counts = sum(counts) by 1\n"
            + "| project counts";

    private final String QUERY_TOTAL_TARGET_ERROR = "analytics_target_error_summary\n"
            + "| where AGG_WINDOW_START_TIME > datetime(${from}) and AGG_WINDOW_START_TIME < datetime"
            + "(${to})"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerID == \"${customerId}\""
            + "| summarize counts = sum(counts) by 1\n"
            + "| project counts";

    private final String QUERY_TOTAL_PROXY_ERROR = "analytics_proxy_error_summary\n"
            + "| where AGG_WINDOW_START_TIME > datetime(${from}) and AGG_WINDOW_START_TIME < datetime"
            + "(${to})"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerID == \"${customerId}\""
            + "| summarize counts = sum(counts) by 1\n"
            + "| project counts";

    private final String QUERY_MAX_LATENCY = "analytics_latency_summary\n"
            + "| where AGG_WINDOW_START_TIME > datetime(${from}) and AGG_WINDOW_START_TIME < datetime"
            + "(${to})"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerID == \"${customerId}\""
            + "| summarize counts = max(responseLatency) by 1"
            + "| project counts";

    private final String QUERY_LATENCY_SUMMARY = "analytics_latency_summary\n"
            + "| where AGG_WINDOW_START_TIME > datetime(${from}) and AGG_WINDOW_START_TIME < datetime"
            + "(${to})"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerID == \"${customerId}\""
            + "| extend timeSpan = AGG_WINDOW_START_TIME"
            + "| summarize latencyTime = max(responseLatency) by bin(timeSpan, 1m)";

    private final String QUERY_REQUEST_SUMMARY = "analytics_request_summary\n"
            + "| where AGG_WINDOW_START_TIME > datetime(${from}) and AGG_WINDOW_START_TIME < datetime"
            + "(${to})"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerID == \"${customerId}\""
            + "| extend timeSpan = AGG_WINDOW_START_TIME"
            + "| summarize requestCount = sum(counts) by bin(timeSpan, 1m)";

    private final String QUERY_PROXY_ERROR_SUMMARY = "analytics_proxy_error_summary\n"
            + "| where AGG_WINDOW_START_TIME > datetime(${from}) and AGG_WINDOW_START_TIME < datetime"
            + "(${to})"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerID == \"${customerId}\""
            + "| extend timeSpan = AGG_WINDOW_START_TIME"
            + "| summarize errorCount = sum(counts) by bin(timeSpan, 1m)";

    private final String QUERY_TARGET_ERROR_SUMMARY = "analytics_target_error_summary\n"
            + "| where AGG_WINDOW_START_TIME > datetime(${from}) and AGG_WINDOW_START_TIME < datetime"
            + "(${to})"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerID == \"${customerId}\""
            + "| extend timeSpan = AGG_WINDOW_START_TIME"
            + "| summarize errorCount = max(counts) by bin(timeSpan, 1m)";

    public int getTotalTraffic(JWTUserDetails user, String environment, TimeFilter filter) throws QueryException {
        return getTotalCount(user, environment, filter, QUERY_TOTAL_TRAFFIC);
    }

    public int getTotalTargetError(JWTUserDetails user, String environment, TimeFilter filter) throws QueryException {
        return getTotalCount(user, environment, filter, QUERY_TOTAL_TARGET_ERROR);
    }

    public int getTotalProxyError(JWTUserDetails user, String environment, TimeFilter filter) throws QueryException {
        return getTotalCount(user, environment, filter, QUERY_TOTAL_PROXY_ERROR);
    }

    public int getMaxLatency(JWTUserDetails user, String environment, TimeFilter filter) throws QueryException {
        return getTotalCount(user, environment, filter, QUERY_MAX_LATENCY);
    }

    public List<LatencySummary> getLatencySummary(JWTUserDetails user, String environment, TimeFilter filter)
            throws QueryException {
        Map<String, Object> parasMap = new HashMap<>() {
            {
                put("environment", environment);
                put("from", filter.getFrom());
                put("to", filter.getTo());
            }
        };
        String query = QueryProcessor.applyUserDetails(QUERY_LATENCY_SUMMARY, user, parasMap);
        List<Map<String, Object>> map = KustoQueryClient.getInstance().execute(query);
        return Utils.convertTo(map, new TypeReference<>(){});
    }

    public List<SuccessSummary> getSuccessSummary(JWTUserDetails user, String environment, TimeFilter filter)
            throws QueryException {
        Map<String, Object> parasMap = new HashMap<>() {
            {
                put("environment", environment);
                put("from", filter.getFrom());
                put("to", filter.getTo());
            }
        };
        String query = QueryProcessor.applyUserDetails(QUERY_REQUEST_SUMMARY, user, parasMap);
        List<Map<String, Object>> map = KustoQueryClient.getInstance().execute(query);
        return Utils.convertTo(map, new TypeReference<>(){});
    }

    public List<ErrorSummary> getProxyErrorSummary(JWTUserDetails user, String environment, TimeFilter filter)
            throws QueryException {
        Map<String, Object> parasMap = new HashMap<>() {
            {
                put("environment", environment);
                put("from", filter.getFrom());
                put("to", filter.getTo());
            }
        };
        String query = QueryProcessor.applyUserDetails(QUERY_PROXY_ERROR_SUMMARY, user, parasMap);
        List<Map<String, Object>> map = KustoQueryClient.getInstance().execute(query);
        return Utils.convertTo(map, new TypeReference<>(){});
    }

    public List<ErrorSummary> getTargetErrorSummary(JWTUserDetails user, String environment, TimeFilter filter)
            throws QueryException {
        Map<String, Object> parasMap = new HashMap<>() {
            {
                put("environment", environment);
                put("from", filter.getFrom());
                put("to", filter.getTo());
            }
        };
        String query = QueryProcessor.applyUserDetails(QUERY_TARGET_ERROR_SUMMARY, user, parasMap);
        List<Map<String, Object>> map = KustoQueryClient.getInstance().execute(query);
        return Utils.convertTo(map, new TypeReference<>(){});
    }

    private int getTotalCount(JWTUserDetails user, String environment, TimeFilter filter, String queryTemplate) throws QueryException {
        Map<String, Object> parasMap = new HashMap<>() {
            {
                put("environment", environment);
                put("from", filter.getFrom());
                put("to", filter.getTo());
            }
        };
        String query = QueryProcessor.applyUserDetails(queryTemplate, user, parasMap);
        List<Map<String, Object>> map = KustoQueryClient.getInstance().execute(query);
        if (map.size() > 0) {
            return (int) map.get(0).get("counts");
        }
        return 0;
    }

}

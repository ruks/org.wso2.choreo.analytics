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

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wso2.choreo.analytics.gql.API;
import org.wso2.choreo.analytics.gql.Application;
import org.wso2.choreo.analytics.gql.Provider;
import org.wso2.choreo.analytics.gql.security.JWTUserDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UtilQueryExecutor extends KustoQueryClient {
    private static final Logger log = LoggerFactory.getLogger(UtilQueryExecutor.class);

    private final String QUERY_LIST_API = "analytics_poc_pipeline_request_test\n"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerId == \"${customerId}\"\n"
            + "| summarize by apiId, apiName, apiVersion, apiCreator\n"
            + "| project-rename id = apiId, name = apiName, version = apiVersion, provider = apiCreator\n"
            + "| top 10 by name asc \n";
    private final String QUERY_LIST_API_OF_PROVIDER = "analytics_poc_pipeline_request_test\n"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerId == \"${customerId}\" and apiCreator =="
            + " \"${apiCreator}\"\n"
            + "| summarize by apiId, apiName, apiVersion, apiCreator\n"
            + "| project-rename id = apiId, name = apiName, version = apiVersion, provider = apiCreator\n"
            + "| top 10 by name asc \n";

    private final String QUERY_LIST_APPLICATIONS = "analytics_poc_pipeline_request_test\n"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerId == \"${customerId}\"\n"
            + "| summarize by applicationId, applicationName, applicationOwner\n"
            + "| project-rename id = applicationId, name = applicationName, owner = applicationOwner\n"
            + "| top 10 by name asc \n";

    private final String QUERY_LIST_APPLICATIONS_BY_OWNER = "analytics_poc_pipeline_request_test\n"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerId == \"${customerId}\" and "
            + "applicationOwner == \"${owner}\"\n"
            + "| summarize by applicationId, applicationName, applicationOwner\n"
            + "| project-rename id = applicationId, name = applicationName, owner = applicationOwner\n"
            + "| top 10 by name asc \n";

    private final String QUERY_LIST_PROVIDERS = "analytics_poc_pipeline_request_test\n"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerId == \"${customerId}\"\n"
            + "| distinct apiCreator\n"
            + "| project-rename name = apiCreator\n"
            + "| top 10 by name asc ";

    public List<API> getAllApis(JWTUserDetails user, String environment) throws QueryException {
        Map<String, Object> parasMap = new HashMap<>() {
            {
                     put("environment", environment);
            }
        };
        String query = QueryProcessor.applyUserDetails(QUERY_LIST_API, user, parasMap);
        List<Map<String, Object>> map = execute(query);
        return convertTo(map, new TypeReference<>(){});
    }

    public List<API> getAllApis(JWTUserDetails user, String environment, String provider)
            throws QueryException {
        Map<String, Object> parasMap = new HashMap<>() {
            {
                put("environment", environment);
                put("apiCreator", provider);
            }
        };
        String query = QueryProcessor.applyUserDetails(QUERY_LIST_API_OF_PROVIDER, user, parasMap);
        List<Map<String, Object>> map = execute(query);
        return convertTo(map, new TypeReference<>(){});
    }

    public List<Application> getAllApplications(JWTUserDetails user, String environment)
            throws QueryException {
        Map<String, Object> parasMap = new HashMap<>() {
            {
                put("environment", environment);
            }
        };
        String query = QueryProcessor.applyUserDetails(QUERY_LIST_APPLICATIONS, user, parasMap);
        List<Map<String, Object>> map = execute(query);
        return convertTo(map, new TypeReference<>(){});
    }

    public List<Application> getAllApplications(JWTUserDetails user, String environment, String owner)
            throws QueryException {
        Map<String, Object> parasMap = new HashMap<>() {
            {
                put("environment", environment);
                put("owner", owner);
            }
        };
        String query = QueryProcessor.applyUserDetails(QUERY_LIST_APPLICATIONS_BY_OWNER, user, parasMap);
        List<Map<String, Object>> map = execute(query);
        return convertTo(map, new TypeReference<>(){});
    }

    public List<Provider> getProviders(JWTUserDetails user, String environment)
            throws QueryException {
        Map<String, Object> parasMap = new HashMap<>() {
            {
                put("environment", environment);
            }
        };
        String query = QueryProcessor.applyUserDetails(QUERY_LIST_PROVIDERS, user, parasMap);
        List<Map<String, Object>> map = execute(query);
        return convertTo(map, new TypeReference<>(){});
    }
}

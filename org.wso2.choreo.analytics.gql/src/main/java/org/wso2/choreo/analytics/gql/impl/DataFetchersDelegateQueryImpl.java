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

package org.wso2.choreo.analytics.gql.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.wso2.choreo.analytics.gql.API;
import org.wso2.choreo.analytics.gql.APIAlertConfig;
import org.wso2.choreo.analytics.gql.AlertSubscription;
import org.wso2.choreo.analytics.gql.ApiAvailability;
import org.wso2.choreo.analytics.gql.ApiErrorSummary;
import org.wso2.choreo.analytics.gql.ApiLatencySummary;
import org.wso2.choreo.analytics.gql.AppAlertConfig;
import org.wso2.choreo.analytics.gql.Application;
import org.wso2.choreo.analytics.gql.DataFetchersDelegateQuery;
import org.wso2.choreo.analytics.gql.Environment;
import org.wso2.choreo.analytics.gql.ErrorsByCategory;
import org.wso2.choreo.analytics.gql.ErrorsMap;
import org.wso2.choreo.analytics.gql.GatewayErrorsOverTime;
import org.wso2.choreo.analytics.gql.LatencySummary;
import org.wso2.choreo.analytics.gql.TargetErrorsOverTime;
import org.wso2.choreo.analytics.gql.TotalTrafficFilter;
import org.wso2.choreo.analytics.gql.alert.AlertDAO;
import org.wso2.choreo.analytics.gql.kusto.KustoQueryClient;
import org.wso2.choreo.analytics.gql.security.JWTUserDetails;
import org.wso2.choreo.analytics.gql.security.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class DataFetchersDelegateQueryImpl implements DataFetchersDelegateQuery {
    private final UserService userService;
    private KustoQueryClient kustoQueryClient;

    public DataFetchersDelegateQueryImpl(UserService userService, KustoQueryClient kustoQueryClient) {
        this.userService = userService;
        this.kustoQueryClient = kustoQueryClient;
    }

    @Override
    public ApiErrorSummary getApiErrorSummary(DataFetchingEnvironment dataFetchingEnvironment, Integer from, Integer to,
            Integer limit, String orderBy, Boolean asc) {
        return null;
    }

    @Override
    public List<ApiLatencySummary> getApiLatencySummary(DataFetchingEnvironment dataFetchingEnvironment, String from,
            String to, Integer limit, String orderBy, Boolean asc, String apiName, String apiVersion,
            String apiResourceTemplate, String apiMethod) {
        ApiLatencySummary summary = new ApiLatencySummary();
        summary.setId("1231312321L");
        return Arrays.asList(summary);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public List<API> listAllAPI(DataFetchingEnvironment dataFetchingEnvironment, String provider) {
        JWTUserDetails user = userService.getCurrentUser();
        List list;
        if(provider == null) {
            list = kustoQueryClient.getAllApis(user);
        } else {
            list = kustoQueryClient.getAllApis(user, provider);
        }

        ObjectMapper mapper = new ObjectMapper();
        List<API> apis = mapper.convertValue(list, new TypeReference<>() {});

        return apis;
    }

    @Override
    public List<String> listVersion(DataFetchingEnvironment dataFetchingEnvironment, String apiName) {
        return null;
    }

    @Override
    public List<Application> listApplications(DataFetchingEnvironment dataFetchingEnvironment,
            String applicationOwner) {
        return null;
    }

    @Override
    public List<String> listProviders(DataFetchingEnvironment dataFetchingEnvironment) {
        return null;
    }

    @Override
    public List<APIAlertConfig> getAllAPIAlertConfig(DataFetchingEnvironment dataFetchingEnvironment) {
        return null;
    }

    @Override
    public List<AppAlertConfig> getAllAppAlertConfig(DataFetchingEnvironment dataFetchingEnvironment) {
        return null;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public AlertSubscription getAlertSubscription(DataFetchingEnvironment dataFetchingEnvironment) {
        JWTUserDetails user = userService.getCurrentUser();
        AlertDAO dao = AlertDAO.getInstance();
        return dao.getAlertSubscription(user);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Integer getTotalTraffic(DataFetchingEnvironment dataFetchingEnvironment, TotalTrafficFilter filter,
            Environment environment) {
        return null;
    }

    @Override
    public Integer getAvgErrorRate(DataFetchingEnvironment dataFetchingEnvironment, String from, String to) {
        return null;
    }

    @Override
    public Integer getOverallLatencyv(DataFetchingEnvironment dataFetchingEnvironment, String from, String to) {
        return null;
    }

    @Override
    public ApiAvailability getApiAvailability(DataFetchingEnvironment dataFetchingEnvironment, String from, String to) {
        return null;
    }

    @Override
    public LatencySummary getLatencySummary(DataFetchingEnvironment dataFetchingEnvironment, String from, String to) {
        return null;
    }

    @Override
    public TargetErrorsOverTime getTargetErrorsOverTime(DataFetchingEnvironment dataFetchingEnvironment, String from,
            String to, String apiId, String appId) {
        return null;
    }

    @Override
    public GatewayErrorsOverTime getGatewayErrorsOverTime(DataFetchingEnvironment dataFetchingEnvironment, String from,
            String to, String apiId, String appId) {
        return null;
    }

    @Override
    public List<ErrorsMap> getTargetErrorsMap(DataFetchingEnvironment dataFetchingEnvironment, String from, String to,
            String apiId, String appId) {
        return null;
    }

    @Override
    public List<ErrorsMap> getGatewayErrorsMap(DataFetchingEnvironment dataFetchingEnvironment, String from, String to,
            String apiId, String appId) {
        return null;
    }

    @Override
    public ErrorsByCategory getErrorsByCategory(DataFetchingEnvironment dataFetchingEnvironment, String from, String to,
            String apiId) {
        return null;
    }
}

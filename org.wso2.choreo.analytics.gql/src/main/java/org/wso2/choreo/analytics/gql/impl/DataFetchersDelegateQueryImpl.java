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

import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.wso2.choreo.analytics.gql.API;
import org.wso2.choreo.analytics.gql.APIAlertConfig;
import org.wso2.choreo.analytics.gql.APIUsageFilter;
import org.wso2.choreo.analytics.gql.AlertSubscription;
import org.wso2.choreo.analytics.gql.ApiAvailability;
import org.wso2.choreo.analytics.gql.AppAlertConfig;
import org.wso2.choreo.analytics.gql.Application;
import org.wso2.choreo.analytics.gql.CacheHits;
import org.wso2.choreo.analytics.gql.DataFetchersDelegateQuery;
import org.wso2.choreo.analytics.gql.DeviceFilter;
import org.wso2.choreo.analytics.gql.Environment;
import org.wso2.choreo.analytics.gql.ErrorsByCategory;
import org.wso2.choreo.analytics.gql.ErrorsByCategoryFilter;
import org.wso2.choreo.analytics.gql.ErrorsMap;
import org.wso2.choreo.analytics.gql.ErrorsOverTimeFilter;
import org.wso2.choreo.analytics.gql.GatewayErrorsOverTime;
import org.wso2.choreo.analytics.gql.IntMap;
import org.wso2.choreo.analytics.gql.Latency;
import org.wso2.choreo.analytics.gql.LatencyFilter;
import org.wso2.choreo.analytics.gql.LatencySummary;
import org.wso2.choreo.analytics.gql.Provider;
import org.wso2.choreo.analytics.gql.ResourceUsage;
import org.wso2.choreo.analytics.gql.ResourceUsageFilter;
import org.wso2.choreo.analytics.gql.TargetErrorsOverTime;
import org.wso2.choreo.analytics.gql.TimeFilter;
import org.wso2.choreo.analytics.gql.alert.AlertDAO;
import org.wso2.choreo.analytics.gql.kusto.QueryException;
import org.wso2.choreo.analytics.gql.kusto.UtilQueryExecutor;
import org.wso2.choreo.analytics.gql.security.JWTUserDetails;
import org.wso2.choreo.analytics.gql.security.UserService;

import java.util.List;

@Component
public class DataFetchersDelegateQueryImpl implements DataFetchersDelegateQuery {
    private static final Logger log = LoggerFactory.getLogger(DataFetchersDelegateQueryImpl.class);
    private final UserService userService;
    private UtilQueryExecutor utilQueryExecutor;

    public DataFetchersDelegateQueryImpl(UserService userService, UtilQueryExecutor utilQueryExecutor) {
        this.userService = userService;
        this.utilQueryExecutor = utilQueryExecutor;
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public List<API> listAllAPI(DataFetchingEnvironment dataFetchingEnvironment, String provider,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        List<API> apis;
        try {
            if (provider == null) {
                apis = utilQueryExecutor.getAllApis(user, environment.getName());
            } else {
                apis = utilQueryExecutor.getAllApis(user, environment.getName(), provider);
            }
            return apis;
        } catch (QueryException e) {
            log.error("Error while getting APIs.", e);
            throw new DataFetchingException("Error while getting APIs.");
        }
    }

    @Override
    public List<String> listVersion(DataFetchingEnvironment dataFetchingEnvironment, String apiName,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<Application> listApplications(DataFetchingEnvironment dataFetchingEnvironment, String owner,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        List<Application> applications;
        try {
            if (owner == null) {
                applications = utilQueryExecutor.getAllApplications(user, environment.getName());
            } else {
                applications = utilQueryExecutor.getAllApplications(user, environment.getName(), owner);
            }
            return applications;
        } catch (QueryException e) {
            log.error("Error while getting applications.", e);
            throw new DataFetchingException("Error while getting applications.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<Provider> listProviders(DataFetchingEnvironment dataFetchingEnvironment, Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return utilQueryExecutor.getProviders(user, environment.getName());
        } catch (QueryException e) {
            log.error("Error while getting providers.", e);
            throw new DataFetchingException("Error while getting providers.");
        }
    }

    @Override
    public List<APIAlertConfig> getAllAPIAlertConfig(DataFetchingEnvironment dataFetchingEnvironment,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<AppAlertConfig> getAllAppAlertConfig(DataFetchingEnvironment dataFetchingEnvironment,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public AlertSubscription getAlertSubscription(DataFetchingEnvironment dataFetchingEnvironment,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        AlertDAO dao = AlertDAO.getInstance();
        return dao.getAlertSubscription(user);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Integer getTotalTraffic(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public Integer getAvgErrorRate(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public Integer getOverallLatency(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public ApiAvailability getApiAvailability(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public LatencySummary getLatencySummary(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<TargetErrorsOverTime> getTargetErrorsOverTime(DataFetchingEnvironment dataFetchingEnvironment,
            ErrorsOverTimeFilter filter, Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<GatewayErrorsOverTime> getGatewayErrorsOverTime(DataFetchingEnvironment dataFetchingEnvironment,
            ErrorsOverTimeFilter filter, Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<ErrorsMap> getTargetErrorsMap(DataFetchingEnvironment dataFetchingEnvironment,
            ErrorsOverTimeFilter filter, Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<ErrorsMap> getGatewayErrorsMap(DataFetchingEnvironment dataFetchingEnvironment,
            ErrorsOverTimeFilter filter, Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<ErrorsByCategory> getErrorsByCategory(DataFetchingEnvironment dataFetchingEnvironment,
            ErrorsByCategoryFilter filter, Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<IntMap> getAPIUsageOverTime(DataFetchingEnvironment dataFetchingEnvironment, APIUsageFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<IntMap> getAPIUsageByApp(DataFetchingEnvironment dataFetchingEnvironment, APIUsageFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<IntMap> getAPIUsageByAppOverTime(DataFetchingEnvironment dataFetchingEnvironment, APIUsageFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<IntMap> getTargetUsage(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<IntMap> getTargetUsageOverTime(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<ResourceUsage> getResourceUsage(DataFetchingEnvironment dataFetchingEnvironment,
            ResourceUsageFilter filter, Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<IntMap> topSlowestAPIs(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<Latency> getLatency(DataFetchingEnvironment dataFetchingEnvironment, LatencyFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<CacheHits> getCacheHits(DataFetchingEnvironment dataFetchingEnvironment, LatencyFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<IntMap> getTopPlatforms(DataFetchingEnvironment dataFetchingEnvironment, DeviceFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    public List<IntMap> getTopAgents(DataFetchingEnvironment dataFetchingEnvironment, DeviceFilter filter,
            Environment environment) {
        throw new DataFetchingException("Not supported.");
    }
}

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
import org.wso2.choreo.analytics.gql.ErrorSummary;
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
import org.wso2.choreo.analytics.gql.SuccessSummary;
import org.wso2.choreo.analytics.gql.TargetErrorsOverTime;
import org.wso2.choreo.analytics.gql.TimeFilter;
import org.wso2.choreo.analytics.gql.alert.AlertDAO;
import org.wso2.choreo.analytics.gql.alert.AlertDAOException;
import org.wso2.choreo.analytics.gql.kusto.OverviewQueryExecutor;
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
    private OverviewQueryExecutor overviewQueryExecutor;

    public DataFetchersDelegateQueryImpl(UserService userService, UtilQueryExecutor utilQueryExecutor, OverviewQueryExecutor overviewQueryExecutor) {
        this.userService = userService;
        this.utilQueryExecutor = utilQueryExecutor;
        this.overviewQueryExecutor = overviewQueryExecutor;
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
    @PreAuthorize("isAuthenticated()")
    public List<APIAlertConfig> getAllAPIAlertConfig(DataFetchingEnvironment dataFetchingEnvironment,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return AlertDAO.getInstance().getAllAPICreatorAlertConfig(user, environment.getName());
        } catch (AlertDAOException e) {
            log.error("Error while getting API alert configs.", e);
            throw new DataFetchingException("Error while getting API alert configs.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<AppAlertConfig> getAllAppAlertConfig(DataFetchingEnvironment dataFetchingEnvironment,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return AlertDAO.getInstance().getAllSubscriberAlertConfig(user, environment.getName());
        } catch (AlertDAOException e) {
            log.error("Error while getting application alert configs.", e);
            throw new DataFetchingException("Error while getting application alert configs.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public AlertSubscription getAlertSubscription(DataFetchingEnvironment dataFetchingEnvironment,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return AlertDAO.getInstance().getAlertSubscription(user, environment.getName());
        } catch (AlertDAOException e) {
            log.error("Error while getting alert subscriptions.", e);
            throw new DataFetchingException("Error while getting alert subscriptions");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Integer getTotalTraffic(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return overviewQueryExecutor.getTotalTraffic(user, environment.getName(), filter);
        } catch (QueryException e) {
            log.error("Error while getting total traffic.", e);
            throw new DataFetchingException("Error while getting total traffic.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Integer getTotalProxyErrors(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return overviewQueryExecutor.getTotalProxyError(user, environment.getName(), filter);
        } catch (QueryException e) {
            log.error("Error while getting total proxy errors.", e);
            throw new DataFetchingException("Error while getting total proxy errors.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Integer getTotalTargetErrors(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return overviewQueryExecutor.getTotalTargetError(user, environment.getName(), filter);
        } catch (QueryException e) {
            log.error("Error while getting total target errors.", e);
            throw new DataFetchingException("Error while getting total target errors.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Integer getOverallLatency(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return overviewQueryExecutor.getMaxLatency(user, environment.getName(), filter);
        } catch (QueryException e) {
            log.error("Error while getting max latency.", e);
            throw new DataFetchingException("Error while getting max latency.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<LatencySummary> getLatencySummary(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return overviewQueryExecutor.getLatencySummary(user, environment.getName(), filter);
        } catch (QueryException e) {
            log.error("Error while getting latency summary.", e);
            throw new DataFetchingException("Error while getting latency summary.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<SuccessSummary> getSuccessSummary(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return overviewQueryExecutor.getSuccessSummary(user, environment.getName(), filter);
        } catch (QueryException e) {
            log.error("Error while getting success summary.", e);
            throw new DataFetchingException("Error while getting success summary.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ErrorSummary> getTargetErrorSummary(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return overviewQueryExecutor.getTargetErrorSummary(user, environment.getName(), filter);
        } catch (QueryException e) {
            log.error("Error while getting target error summary.", e);
            throw new DataFetchingException("Error while getting target error summary.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ErrorSummary> getProxyErrorSummary(DataFetchingEnvironment dataFetchingEnvironment, TimeFilter filter,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return overviewQueryExecutor.getProxyErrorSummary(user, environment.getName(), filter);
        } catch (QueryException e) {
            log.error("Error while getting proxy error summary.", e);
            throw new DataFetchingException("Error while getting proxy error summary.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<TargetErrorsOverTime> getTargetErrorsOverTime(DataFetchingEnvironment dataFetchingEnvironment,
            ErrorsOverTimeFilter filter, Environment environment) {
        throw new DataFetchingException("Not supported.");
    }

    @Override
    @PreAuthorize("isAuthenticated()")
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

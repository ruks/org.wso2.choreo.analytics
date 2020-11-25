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
import org.wso2.choreo.analytics.gql.APIAlertConfig;
import org.wso2.choreo.analytics.gql.APIAlertConfigInput;
import org.wso2.choreo.analytics.gql.AlertSubscription;
import org.wso2.choreo.analytics.gql.AlertSubscriptionInput;
import org.wso2.choreo.analytics.gql.AppAlertConfig;
import org.wso2.choreo.analytics.gql.AppAlertConfigInput;
import org.wso2.choreo.analytics.gql.DataFetchersDelegateMutation;
import org.wso2.choreo.analytics.gql.Environment;
import org.wso2.choreo.analytics.gql.alert.AlertDAO;
import org.wso2.choreo.analytics.gql.alert.AlertDAOException;
import org.wso2.choreo.analytics.gql.security.JWTUserDetails;
import org.wso2.choreo.analytics.gql.security.UserService;

@Component
public class DataFetchersDelegateMutationImpl implements DataFetchersDelegateMutation {
    private static final Logger log = LoggerFactory.getLogger(DataFetchersDelegateMutationImpl.class);
    private final UserService userService;

    public DataFetchersDelegateMutationImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public APIAlertConfig addAPIAlertConfig(DataFetchingEnvironment dataFetchingEnvironment,
            APIAlertConfigInput alertConfig, Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return AlertDAO.getInstance().updateAPICreatorAlertConfig(user, alertConfig, environment.getName());
        } catch (AlertDAOException e) {
            log.error("Error occurred while adding API alert configurations.", e);
            throw new DataFetchingException("Error occurred while adding API alert configurations.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public AppAlertConfig addAppAlertConfig(DataFetchingEnvironment dataFetchingEnvironment,
            AppAlertConfigInput alertConfig, Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return AlertDAO.getInstance().updateSubscriberAlertConfig(user, alertConfig, environment.getName());
        } catch (AlertDAOException e) {
            log.error("Error occurred while adding application alert configurations.", e);
            throw new DataFetchingException("Error occurred while adding application alert configurations.");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public AlertSubscription subscribeAlert(DataFetchingEnvironment dataFetchingEnvironment,
            AlertSubscriptionInput config, Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return AlertDAO.getInstance().subscribeAlert(user, config, environment.getName());
        } catch (AlertDAOException e) {
            log.error("Error while subscribe to alert.", e);
            throw new DataFetchingException("Error while subscribe to alert.");
        }
    }

    @Override
    public AlertSubscription unSubscribeAlert(DataFetchingEnvironment dataFetchingEnvironment,
            Environment environment) {
        JWTUserDetails user = userService.getCurrentUser();
        try {
            return AlertDAO.getInstance().unSubscribeAlert(user, environment.getName());
        } catch (AlertDAOException e) {
            log.error("Error while un-subscribing alert.", e);
            throw new DataFetchingException("Error while un-subscribing alert.");
        }
    }
}

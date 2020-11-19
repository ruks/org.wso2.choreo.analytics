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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.wso2.choreo.analytics.gql.APIAlertConfig;
import org.wso2.choreo.analytics.gql.APIAlertConfigInput;
import org.wso2.choreo.analytics.gql.AlertSubscription;
import org.wso2.choreo.analytics.gql.AlertSubscriptionInput;
import org.wso2.choreo.analytics.gql.AppAlertConfig;
import org.wso2.choreo.analytics.gql.AppAlertConfigInput;
import org.wso2.choreo.analytics.gql.DataFetchersDelegateMutation;
import org.wso2.choreo.analytics.gql.alert.AlertDAO;
import org.wso2.choreo.analytics.gql.security.JWTUserDetails;
import org.wso2.choreo.analytics.gql.security.UserService;

@Component
public class DataFetchersDelegateMutationImpl implements DataFetchersDelegateMutation {
    private final UserService userService;

    public DataFetchersDelegateMutationImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public APIAlertConfig addAPIAlertConfig(DataFetchingEnvironment dataFetchingEnvironment,
            APIAlertConfigInput alertConfig) {
        return null;
    }

    @Override
    public AppAlertConfig addAppAlertConfig(DataFetchingEnvironment dataFetchingEnvironment,
            AppAlertConfigInput alertConfig) {
        return null;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public AlertSubscription subscribeAlert(DataFetchingEnvironment dataFetchingEnvironment,
            AlertSubscriptionInput config) {
        JWTUserDetails user = userService.getCurrentUser();
        AlertDAO dao = AlertDAO.getInstance();
        return dao.subscribeAlert(user, config);
    }

    @Override
    public AlertSubscription unSubscribeAlert(DataFetchingEnvironment dataFetchingEnvironment, String userId) {
        return null;
    }
}

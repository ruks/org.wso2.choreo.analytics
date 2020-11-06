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

package org.wso2.apimgt.choreo.rest.api.analytics.impl;

import org.wso2.apimgt.choreo.rest.api.analytics.impl.dto.APICreatorAlertConfig;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.dto.AlertSubscription;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.dto.SubscriberAlertConfig;

import java.util.Map;

public class Utils {


    public static APICreatorAlertConfig APICreatorAlertConfigFromMap(Map<String, Object> map) {
        APICreatorAlertConfig config = new APICreatorAlertConfig();
        config.setApiName((String) map.get("apiName"));
        config.setApiVersion((String) map.get("apiVersion"));
        config.setThresholdResponseTime((int) map.get("thresholdResponseTime"));
        config.setThresholdBackendTime((int) map.get("thresholdBackendTime"));
        return config;
    }

    public static SubscriberAlertConfig SubscriberAlertConfigFromMap(Map<String, Object> map) {
        SubscriberAlertConfig config = new SubscriberAlertConfig();
        config.setApiName((String) map.get("apiName"));
        config.setApiVersion((String) map.get("apiVersion"));
        config.setAppName((String) map.get("appName"));
        config.setAppOwner((String) map.get("appOwner"));
        config.setThresholdRequestCount((int) map.get("thresholdRequestCount"));
        return config;
    }

    public static AlertSubscription AlertSubscriptionFromMap(Map<String, Object> map) {
        AlertSubscription config = new AlertSubscription();
        config.setUserId((String) map.get("userId"));
        config.setAlertTypes((String) map.get("alertTypes"));
        config.setEmails((String) map.get("emails"));
        return config;
    }
}

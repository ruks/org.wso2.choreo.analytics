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

import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.security.core.userdetails.UserDetails;
import org.wso2.choreo.analytics.gql.security.JWTUserDetails;

import java.util.HashMap;
import java.util.Map;

public class QueryProcessor {
    public static String listAPI = "analytics_poc_pipeline_request_test\n"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerId == \"${customerId}\"\n"
            + "| summarize by apiId, apiName, apiVersion, apiCreator\n" + "| top 50 by apiName asc \n";
    public static String listAPIOfProvider = "analytics_poc_pipeline_request_test\n"
            + "| where apiCreatorTenantDomain == \"${tenant}\" and customerId == \"${customerId}\"\n"
            + "| summarize by apiId, apiName, apiVersion, apiCreator\n" + "| top 50 by apiName asc \n";


//    public static void main(String[] args) {
//        StrSubstitutor.replaceSystemProperties(
//                "You are running with java.version = ${java.version} and os.name = ${os.name}.");
//        Map valuesMap = new HashMap();
//        valuesMap.put("animal", "quick brown fox");
//        valuesMap.put("target", "lazy dog");
//        String templateString = "The ${animal} jumped over the ${target}.";
//        StrSubstitutor sub = new StrSubstitutor(valuesMap);
//        String resolvedString = sub.replace(templateString);
//        System.out.println(resolvedString);
//    }
//
    public static String getQuery(String query, JWTUserDetails user) {
        Map valuesMap = new HashMap();
        valuesMap.put("tenant", user.getTenant());
        valuesMap.put("customerId", user.getCustomerId());
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String resolvedString = sub.replace(query);
        return resolvedString;
    }
}

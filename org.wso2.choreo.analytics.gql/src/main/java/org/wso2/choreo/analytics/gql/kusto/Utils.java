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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.choreo.analytics.gql.impl.DataFetchingException;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    final static <T> T convertTo(Object o, TypeReference<T> typeReference) throws IllegalArgumentException {
        try {
            T converted = mapper.convertValue(o, typeReference);
            return converted;
        } catch (IllegalArgumentException e) {
            log.error("Error occurred while formatting requested data.", e);
            throw new DataFetchingException("Error occurred while formatting requested data.");
        }
    }
}

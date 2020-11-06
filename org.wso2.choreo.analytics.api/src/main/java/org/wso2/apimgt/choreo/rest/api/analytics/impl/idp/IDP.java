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

package org.wso2.apimgt.choreo.rest.api.analytics.impl.idp;

import io.jsonwebtoken.Jwts;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IDP {
    public static String getToken(String username) throws Exception {
        Key key = getPrivateKey();

        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.HOUR, 1);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "rukshan@wso2.com");
        map.put("tenant", "foo.com");
        map.put("customer", "micron.com");
        map.put("deployment", Arrays.asList("dev", "staging", "prod"));
        String jws = Jwts.builder()
                .setSubject(username)
                .setAudience("http://localhost:9010/choreo/api/analytics")
                .setExpiration(exp.getTime())
                .setIssuedAt(Calendar.getInstance().getTime())
                .setIssuer("https://localhost:9443/oauth2/token")
                .setId(UUID.randomUUID().toString())
                .addClaims(map)
                .signWith(key).compact();
        return jws;
    }

    private static Key getPrivateKey() throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] pwdArray = "wso2carbon".toCharArray();
        InputStream jksStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("security/wso2carbon.jks");
        ks.load(jksStream, pwdArray);
        return ks.getKey("wso2carbon", pwdArray);
    }
}

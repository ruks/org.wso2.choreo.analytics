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

package com.choroe.analytics.portal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private String tokenEndpoint;
    private String authorizeEndpoint;
    private String consumerKey;
    private String consumerSecret;
    private String trustStore;
    private String trustPassword;
    private String loginCallback;
    private String tokenCookieKey1;
    private String tokenCookieKey2;
    private String idTokenCookieKey1;
    private String idTokenCookieKey2;
    private String oidcLogoutURL;
    private String oidcLogoutcallBack;
    private String cookieHostName;

    private static Configuration configuration;

    private Configuration() {
    }

    public synchronized static Configuration getInstance() {
        if (configuration == null) {
            configuration = new Configuration();
            configuration.load();
        }
        return configuration;
    }

    private void load() {
        InputStream propStream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("application" + ".properties");
        Properties properties = new Properties();
        try {
            properties.load(propStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        tokenEndpoint = properties.getProperty("tokenEndpoint");
        authorizeEndpoint = properties.getProperty("authorizeEndpoint");
        consumerKey = properties.getProperty("consumerKey");
        consumerSecret = properties.getProperty("consumerSecret");
        trustStore = properties.getProperty("trustStore");
        trustPassword = properties.getProperty("trustPassword");
        loginCallback = properties.getProperty("loginCallback");
        tokenCookieKey1 = properties.getProperty("tokenCookieKey1");
        tokenCookieKey2 = properties.getProperty("tokenCookieKey2");
        idTokenCookieKey1 = properties.getProperty("idTokenCookieKey1");
        idTokenCookieKey2 = properties.getProperty("idTokenCookieKey2");
        oidcLogoutURL = properties.getProperty("oidcLogoutURL");
        oidcLogoutcallBack = properties.getProperty("oidcLogoutcallBack");
        cookieHostName = properties.getProperty("cookieHostName");

        if (trustStore != null && trustPassword != null) {
            System.setProperty("javax.net.ssl.trustStore", trustStore);
            System.setProperty("javax.net.ssl.trustStorePassword", trustPassword);
        }
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public String getAuthorizeEndpoint() {
        return authorizeEndpoint;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public String getTrustPassword() {
        return trustPassword;
    }

    public String getLoginCallback() {
        return loginCallback;
    }

    public String getTokenCookieKey1() {
        return tokenCookieKey1;
    }

    public String getTokenCookieKey2() {
        return tokenCookieKey2;
    }

    public String getIdTokenCookieKey1() {
        return idTokenCookieKey1;
    }

    public String getIdTokenCookieKey2() {
        return idTokenCookieKey2;
    }

    public String getOidcLogoutURL() {
        return oidcLogoutURL;
    }

    public String getOidcLogoutcallBack() {
        return oidcLogoutcallBack;
    }

    public String getCookieHostName() {
        return cookieHostName;
    }
}

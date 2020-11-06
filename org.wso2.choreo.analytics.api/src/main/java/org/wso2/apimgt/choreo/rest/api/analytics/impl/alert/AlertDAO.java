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

package org.wso2.apimgt.choreo.rest.api.analytics.impl.alert;

import org.wso2.apimgt.choreo.rest.api.analytics.impl.config.ConfigHolder;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.config.Datasource;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.dto.APICreatorAlertConfig;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.dto.AlertSubscription;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.dto.SubscriberAlertConfig;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.interceptor.AuthenticationContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlertDAO {

    private String driver;
    private String dbURL;
    private String username;
    private String password;
    private static AlertDAO dao = new AlertDAO();

    private AlertDAO() {
        Datasource datasource = ConfigHolder.getInstance().getConfiguration().getDatasource();
        driver = datasource.getDriver();
        dbURL = datasource.getUrl();
        username = datasource.getUsername();
        password = datasource.getPassword();
    }

    public static AlertDAO getInstance() {
        return dao;
    }

    private Connection getConnection() {
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(dbURL, username, password);
            return conn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public APICreatorAlertConfig updateAPICreatorAlertConfig(AuthenticationContext context,
            APICreatorAlertConfig config) {
        String query = "begin tran\n"
                + "if exists (select * from API_ALERT_CONFIG with (updlock,serializable) WHERE API_NAME = ? and API_VERSION = ? and TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?)\n"
                + "begin\n"
                + "   UPDATE API_ALERT_CONFIG SET API_NAME = ?, API_VERSION = ?, TENANT = ?, DEPLOYMENT_ID = ?, CUSTOMER_ID = ?, THRESHOLD_RESPONSE_TIME = ?, THRESHOLD_BACKEND_TIME = ? WHERE API_NAME = ? and API_VERSION = ? and TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?;\n"
                + "end\n" + "else\n" + "begin\n"
                + "   INSERT INTO API_ALERT_CONFIG (API_NAME, API_VERSION, TENANT, DEPLOYMENT_ID, CUSTOMER_ID, THRESHOLD_RESPONSE_TIME, THRESHOLD_BACKEND_TIME)\n"
                + "VALUES (?, ?, ?, ?, ?, ?, ?);\n" + "end\n" + "commit tran";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            int i = 1;
            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, context.getTenant());
            statement.setString(i++, context.getDeploymentId());
            statement.setString(i++, context.getCustomerId());

            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, context.getTenant());
            statement.setString(i++, context.getDeploymentId());
            statement.setString(i++, context.getCustomerId());
            statement.setInt(i++, config.getThresholdResponseTime());
            statement.setInt(i++, config.getThresholdBackendTime());

            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, context.getTenant());
            statement.setString(i++, context.getDeploymentId());
            statement.setString(i++, context.getCustomerId());

            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, context.getTenant());
            statement.setString(i++, context.getDeploymentId());
            statement.setString(i++, context.getCustomerId());
            statement.setInt(i++, config.getThresholdResponseTime());
            statement.setInt(i++, config.getThresholdBackendTime());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getAPICreatorAlertConfig(context, config);
    }

    public APICreatorAlertConfig getAPICreatorAlertConfig(AuthenticationContext context, APICreatorAlertConfig config) {
        String query = "select API_NAME, API_VERSION, THRESHOLD_RESPONSE_TIME, THRESHOLD_BACKEND_TIME from API_ALERT_CONFIG WHERE API_NAME = ? and API_VERSION = ? and TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, config.getApiName());
            statement.setString(2, config.getApiVersion());
            statement.setString(3, context.getTenant());
            statement.setString(4, context.getDeploymentId());
            statement.setString(5, context.getCustomerId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    APICreatorAlertConfig alertConfig = new APICreatorAlertConfig();
                    alertConfig.setApiName(resultSet.getString(1));
                    alertConfig.setApiVersion(resultSet.getString(2));
                    alertConfig.setThresholdResponseTime(resultSet.getInt(3));
                    alertConfig.setThresholdBackendTime(resultSet.getInt(4));
                    return alertConfig;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return null;
    }

    public List<APICreatorAlertConfig> getAllAPICreatorAlertConfig(AuthenticationContext context) {
        String query = "select API_NAME, API_VERSION, THRESHOLD_RESPONSE_TIME, THRESHOLD_BACKEND_TIME from API_ALERT_CONFIG WHERE TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, context.getTenant());
            statement.setString(2, context.getDeploymentId());
            statement.setString(3, context.getCustomerId());

            try (ResultSet resultSet = statement.executeQuery()) {
                APICreatorAlertConfig config;
                List<APICreatorAlertConfig> configs = new ArrayList<>();
                while (resultSet.next()) {
                    config = new APICreatorAlertConfig();
                    config.setApiName(resultSet.getString(1));
                    config.setApiVersion(resultSet.getString(2));
                    config.setThresholdResponseTime(resultSet.getInt(3));
                    config.setThresholdBackendTime(resultSet.getInt(4));
                    configs.add(config);
                }
                return configs;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public SubscriberAlertConfig updateSubscriberAlertConfig(AuthenticationContext context,
            SubscriberAlertConfig config) {
        String query = "begin tran\n"
                + "if exists (select * from APP_ALERT_CONFIG with (updlock,serializable) WHERE API_NAME = ? and API_VERSION = ? and APP_NAME = ? and APP_OWNER = ? and TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?)\n"
                + "begin\n"
                + "   UPDATE APP_ALERT_CONFIG SET API_NAME = ?, API_VERSION = ?, APP_NAME = ?, APP_OWNER = ?, TENANT "
                + "= ?, DEPLOYMENT_ID = ?, CUSTOMER_ID = ?, THRESHOLD_REQUEST_COUNT = ? WHERE API_NAME = ? and API_VERSION = ? and APP_NAME = ? and APP_OWNER = ? and TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?;\n"
                + "end\n" + "else\n" + "begin\n"
                + "   INSERT INTO APP_ALERT_CONFIG (API_NAME, API_VERSION, APP_NAME, APP_OWNER, TENANT, DEPLOYMENT_ID, CUSTOMER_ID, THRESHOLD_REQUEST_COUNT)\n"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);\n" + "end\n" + "commit tran";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            int i = 1;
            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, config.getAppName());
            statement.setString(i++, config.getAppOwner());
            statement.setString(i++, context.getTenant());
            statement.setString(i++, context.getDeploymentId());
            statement.setString(i++, context.getCustomerId());

            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, config.getAppName());
            statement.setString(i++, config.getAppOwner());
            statement.setString(i++, context.getTenant());
            statement.setString(i++, context.getDeploymentId());
            statement.setString(i++, context.getCustomerId());
            statement.setInt(i++, config.getThresholdRequestCount());

            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, config.getAppName());
            statement.setString(i++, config.getAppOwner());
            statement.setString(i++, context.getTenant());
            statement.setString(i++, context.getDeploymentId());
            statement.setString(i++, context.getCustomerId());

            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, config.getAppName());
            statement.setString(i++, config.getAppOwner());
            statement.setString(i++, context.getTenant());
            statement.setString(i++, context.getDeploymentId());
            statement.setString(i++, context.getCustomerId());
            statement.setInt(i++, config.getThresholdRequestCount());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getSubscriberAlertConfig(context, config);
    }

    public SubscriberAlertConfig getSubscriberAlertConfig(AuthenticationContext context, SubscriberAlertConfig config) {
        String query = "select API_NAME, API_VERSION, APP_NAME, APP_OWNER, THRESHOLD_REQUEST_COUNT from APP_ALERT_CONFIG WHERE API_NAME = ? and API_VERSION = ? and APP_NAME = ? and APP_OWNER = ? and TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, config.getApiName());
            statement.setString(2, config.getApiVersion());
            statement.setString(3, config.getAppName());
            statement.setString(4, config.getAppOwner());
            statement.setString(5, context.getTenant());
            statement.setString(6, context.getDeploymentId());
            statement.setString(7, context.getCustomerId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    SubscriberAlertConfig alertConfig = new SubscriberAlertConfig();
                    alertConfig.setApiName(resultSet.getString(1));
                    alertConfig.setApiVersion(resultSet.getString(2));
                    alertConfig.setAppName(resultSet.getString(3));
                    alertConfig.setAppOwner(resultSet.getString(4));
                    alertConfig.setThresholdRequestCount(resultSet.getInt(5));
                    return alertConfig;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return null;
    }

    public List<SubscriberAlertConfig> getAllSubscriberAlertConfig(AuthenticationContext context) {
        String query = "select API_NAME, API_VERSION, APP_NAME, APP_OWNER, THRESHOLD_REQUEST_COUNT from "
                + "APP_ALERT_CONFIG WHERE TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?;";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, context.getTenant());
            statement.setString(2, context.getDeploymentId());
            statement.setString(3, context.getCustomerId());
            try (ResultSet resultSet = statement.executeQuery()) {
                SubscriberAlertConfig alertConfig;
                List<SubscriberAlertConfig> configs = new ArrayList<>();
                while (resultSet.next()) {
                    alertConfig = new SubscriberAlertConfig();
                    alertConfig.setApiName(resultSet.getString(1));
                    alertConfig.setApiVersion(resultSet.getString(2));
                    alertConfig.setAppName(resultSet.getString(3));
                    alertConfig.setAppOwner(resultSet.getString(4));
                    alertConfig.setThresholdRequestCount(resultSet.getInt(5));
                    configs.add(alertConfig);
                }
                return configs;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public AlertSubscription subscribeAlert(AuthenticationContext context, AlertSubscription config) {
        String query = "begin tran\n"
                + "if exists (select * from API_ALERT_SUBSCRIPTION with (updlock,serializable) WHERE USER_ID = ? and TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?)\n"
                + "begin\n"
                + "   UPDATE API_ALERT_SUBSCRIPTION SET USER_ID = ?, ALERT_TYPES = ?, EMAILS = ?, TENANT = ?, DEPLOYMENT_ID = ?, CUSTOMER_ID = ? WHERE USER_ID = ? and TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?;\n"
                + "end\n" + "else\n" + "begin\n"
                + "   INSERT INTO API_ALERT_SUBSCRIPTION (USER_ID, ALERT_TYPES, EMAILS, TENANT, DEPLOYMENT_ID, CUSTOMER_ID)\n"
                + "VALUES (?, ?, ?, ?, ?, ?);\n" + "end\n" + "commit tran";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            int i = 1;
            statement.setString(i++, config.getUserId());
            statement.setString(i++, context.getTenant());
            statement.setString(i++, context.getDeploymentId());
            statement.setString(i++, context.getCustomerId());

            statement.setString(i++, config.getUserId());
            statement.setString(i++, config.getAlertTypes());
            statement.setString(i++, config.getEmails());
            statement.setString(i++, context.getTenant());
            statement.setString(i++, context.getDeploymentId());
            statement.setString(i++, context.getCustomerId());

            statement.setString(i++, config.getUserId());
            statement.setString(i++, context.getTenant());
            statement.setString(i++, context.getDeploymentId());
            statement.setString(i++, context.getCustomerId());

            statement.setString(i++, config.getUserId());
            statement.setString(i++, config.getAlertTypes());
            statement.setString(i++, config.getEmails());
            statement.setString(i++, context.getTenant());
            statement.setString(i++, context.getDeploymentId());
            statement.setString(i++, context.getCustomerId());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getAlertSubscription(context, config.getUserId());
    }

    public AlertSubscription unSubscribeAlert(AuthenticationContext context, String userId) {
        String query = "delete from API_ALERT_SUBSCRIPTION where USER_ID = ? and TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userId);
            statement.setString(2, context.getTenant());
            statement.setString(3, context.getDeploymentId());
            statement.setString(4, context.getCustomerId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getAlertSubscription(context, userId);
    }

    public AlertSubscription getAlertSubscription(AuthenticationContext context, String userId) {
        String query = "select USER_ID, ALERT_TYPES, EMAILS from API_ALERT_SUBSCRIPTION WHERE USER_ID = ? and TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userId);
            statement.setString(2, context.getTenant());
            statement.setString(3, context.getDeploymentId());
            statement.setString(4, context.getCustomerId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    AlertSubscription alertConfig = new AlertSubscription();
                    alertConfig.setUserId(resultSet.getString(1));
                    alertConfig.setAlertTypes(resultSet.getString(2));
                    alertConfig.setEmails(resultSet.getString(3));
                    return alertConfig;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return null;
    }
}



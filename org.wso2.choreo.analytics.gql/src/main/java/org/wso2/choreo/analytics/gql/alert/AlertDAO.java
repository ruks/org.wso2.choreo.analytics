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

package org.wso2.choreo.analytics.gql.alert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.choreo.analytics.gql.APIAlertConfig;
import org.wso2.choreo.analytics.gql.APIAlertConfigInput;
import org.wso2.choreo.analytics.gql.AlertSubscription;
import org.wso2.choreo.analytics.gql.AlertSubscriptionInput;
import org.wso2.choreo.analytics.gql.AppAlertConfig;
import org.wso2.choreo.analytics.gql.AppAlertConfigInput;
import org.wso2.choreo.analytics.gql.config.ConfigHolder;
import org.wso2.choreo.analytics.gql.config.Datasource;
import org.wso2.choreo.analytics.gql.security.JWTUserDetails;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlertDAO {
    private static final Logger log = LoggerFactory.getLogger(AlertDAO.class);
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

    private Connection getConnection() throws SQLException, AlertDAOException {
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(dbURL, username, password);
            conn.setAutoCommit(true);
            return conn;
        } catch (ClassNotFoundException e) {
            log.error("DB driver class not found.", e);
            throw new AlertDAOException("Error getting DB connection", e);
        }
    }

    public APIAlertConfig updateAPICreatorAlertConfig(JWTUserDetails user, APIAlertConfigInput config, String environment)
            throws AlertDAOException {
        String query = "begin tran\n"
                + "if exists (select * from API_ALERT_CONFIG with (updlock,serializable) WHERE API_NAME = ? and "
                + "API_VERSION = ? and TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?)\n" + "begin\n"
                + "   UPDATE API_ALERT_CONFIG SET API_NAME = ?, API_VERSION = ?, TENANT = ?, DEPLOYMENT_ID = ?, "
                + "CUSTOMER_ID = ?, THRESHOLD_RESPONSE_TIME = ?, THRESHOLD_BACKEND_TIME = ? WHERE API_NAME = ? and "
                + "API_VERSION = ? and TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?;\n" + "end\n" + "else\n"
                + "begin\n"
                + "   INSERT INTO API_ALERT_CONFIG (API_NAME, API_VERSION, TENANT, DEPLOYMENT_ID, CUSTOMER_ID, "
                + "THRESHOLD_RESPONSE_TIME, THRESHOLD_BACKEND_TIME)\n" + "VALUES (?, ?, ?, ?, ?, ?, ?);\n" + "end\n"
                + "commit tran";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            int i = 1;
            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, user.getTenant());
            statement.setString(i++, environment);
            statement.setString(i++, user.getCustomerId());

            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, user.getTenant());
            statement.setString(i++, environment);
            statement.setString(i++, user.getCustomerId());
            statement.setInt(i++, config.getThresholdResponseTime());
            statement.setInt(i++, config.getThresholdBackendTime());

            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, user.getTenant());
            statement.setString(i++, environment);
            statement.setString(i++, user.getCustomerId());

            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, user.getTenant());
            statement.setString(i++, environment);
            statement.setString(i++, user.getCustomerId());
            statement.setInt(i++, config.getThresholdResponseTime());
            statement.setInt(i, config.getThresholdBackendTime());
            statement.execute();
        } catch (SQLException e) {
            log.error("Error updating API alert config.", e);
            throw new AlertDAOException("Error updating API alert config.", e);
        }

        return getAPICreatorAlertConfig(user, config, environment);
    }

    public APIAlertConfig getAPICreatorAlertConfig(JWTUserDetails user, APIAlertConfigInput config, String environment)
            throws AlertDAOException {
        String query =
                "select API_NAME, API_VERSION, THRESHOLD_RESPONSE_TIME, THRESHOLD_BACKEND_TIME from API_ALERT_CONFIG "
                        + "WHERE API_NAME = ? and API_VERSION = ? and TENANT = ? and DEPLOYMENT_ID = ? and "
                        + "CUSTOMER_ID = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, config.getApiName());
            statement.setString(2, config.getApiVersion());
            statement.setString(3, user.getTenant());
            statement.setString(4, environment);
            statement.setString(5, user.getCustomerId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    APIAlertConfig alertConfig = new APIAlertConfig();
                    alertConfig.setApiName(resultSet.getString(1));
                    alertConfig.setApiVersion(resultSet.getString(2));
                    alertConfig.setThresholdResponseTime(resultSet.getInt(3));
                    alertConfig.setThresholdBackendTime(resultSet.getInt(4));
                    return alertConfig;
                }
            }
        } catch (SQLException e) {
            log.error("Error getting API alert config.", e);
            throw new AlertDAOException("Error getting API alert config.", e);
        }
        return null;
    }

    public List<APIAlertConfig> getAllAPICreatorAlertConfig(JWTUserDetails user, String environment) throws AlertDAOException {
        String query =
                "select API_NAME, API_VERSION, THRESHOLD_RESPONSE_TIME, THRESHOLD_BACKEND_TIME from API_ALERT_CONFIG "
                        + "WHERE TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getTenant());
            statement.setString(2, environment);
            statement.setString(3, user.getCustomerId());

            try (ResultSet resultSet = statement.executeQuery()) {
                APIAlertConfig config;
                List<APIAlertConfig> configs = new ArrayList<>();
                while (resultSet.next()) {
                    config = new APIAlertConfig();
                    config.setApiName(resultSet.getString(1));
                    config.setApiVersion(resultSet.getString(2));
                    config.setThresholdResponseTime(resultSet.getInt(3));
                    config.setThresholdBackendTime(resultSet.getInt(4));
                    configs.add(config);
                }
                return configs;
            }
        } catch (SQLException e) {
            log.error("Error updating all API alert config.", e);
            throw new AlertDAOException("Error updating all API alert config.", e);
        }
    }

    public AppAlertConfig updateSubscriberAlertConfig(JWTUserDetails user, AppAlertConfigInput config, String environment)
            throws AlertDAOException {
        String query = "begin tran\n"
                + "if exists (select * from APP_ALERT_CONFIG with (updlock,serializable) WHERE API_NAME = ? and "
                + "API_VERSION = ? and APP_NAME = ? and APP_OWNER = ? and TENANT = ? and DEPLOYMENT_ID = ? and "
                + "CUSTOMER_ID = ?)\n" + "begin\n"
                + "   UPDATE APP_ALERT_CONFIG SET API_NAME = ?, API_VERSION = ?, APP_NAME = ?, APP_OWNER = ?, TENANT "
                + "= ?, DEPLOYMENT_ID = ?, CUSTOMER_ID = ?, THRESHOLD_REQUEST_COUNT = ? WHERE API_NAME = ? and "
                + "API_VERSION = ? and APP_NAME = ? and APP_OWNER = ? and TENANT = ? and DEPLOYMENT_ID = ? and "
                + "CUSTOMER_ID = ?;\n" + "end\n" + "else\n" + "begin\n"
                + "   INSERT INTO APP_ALERT_CONFIG (API_NAME, API_VERSION, APP_NAME, APP_OWNER, TENANT, "
                + "DEPLOYMENT_ID, CUSTOMER_ID, THRESHOLD_REQUEST_COUNT)\n" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);\n"
                + "end\n" + "commit tran";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            int i = 1;
            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, config.getAppName());
            statement.setString(i++, config.getAppOwner());
            statement.setString(i++, user.getTenant());
            statement.setString(i++, environment);
            statement.setString(i++, user.getCustomerId());

            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, config.getAppName());
            statement.setString(i++, config.getAppOwner());
            statement.setString(i++, user.getTenant());
            statement.setString(i++, environment);
            statement.setString(i++, user.getCustomerId());
            statement.setInt(i++, config.getThresholdRequestCount());

            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, config.getAppName());
            statement.setString(i++, config.getAppOwner());
            statement.setString(i++, user.getTenant());
            statement.setString(i++, environment);
            statement.setString(i++, user.getCustomerId());

            statement.setString(i++, config.getApiName());
            statement.setString(i++, config.getApiVersion());
            statement.setString(i++, config.getAppName());
            statement.setString(i++, config.getAppOwner());
            statement.setString(i++, user.getTenant());
            statement.setString(i++, environment);
            statement.setString(i++, user.getCustomerId());
            statement.setInt(i, config.getThresholdRequestCount());
            statement.execute();
        } catch (SQLException e) {
            log.error("Error updating subscription alert config.", e);
            throw new AlertDAOException("Error updating subscription alert config.", e);
        }

        return getSubscriberAlertConfig(user, config, environment);
    }

    public AppAlertConfig getSubscriberAlertConfig(JWTUserDetails user, AppAlertConfigInput config, String environment)
            throws AlertDAOException {
        String query =
                "select API_NAME, API_VERSION, APP_NAME, APP_OWNER, THRESHOLD_REQUEST_COUNT from APP_ALERT_CONFIG "
                        + "WHERE API_NAME = ? and API_VERSION = ? and APP_NAME = ? and APP_OWNER = ? and TENANT = ? "
                        + "and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, config.getApiName());
            statement.setString(2, config.getApiVersion());
            statement.setString(3, config.getAppName());
            statement.setString(4, config.getAppOwner());
            statement.setString(5, user.getTenant());
            statement.setString(6, environment);
            statement.setString(7, user.getCustomerId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    AppAlertConfig alertConfig = new AppAlertConfig();
                    alertConfig.setApiName(resultSet.getString(1));
                    alertConfig.setApiVersion(resultSet.getString(2));
                    alertConfig.setAppName(resultSet.getString(3));
                    alertConfig.setAppOwner(resultSet.getString(4));
                    alertConfig.setThresholdRequestCount(resultSet.getInt(5));
                    return alertConfig;
                }
            }
        } catch (SQLException e) {
            log.error("Error getting subscription alert config.", e);
            throw new AlertDAOException("Error getting subscription alert config.", e);
        }
        return null;
    }

    public List<AppAlertConfig> getAllSubscriberAlertConfig(JWTUserDetails user, String environment) throws AlertDAOException {
        String query = "select API_NAME, API_VERSION, APP_NAME, APP_OWNER, THRESHOLD_REQUEST_COUNT from "
                + "APP_ALERT_CONFIG WHERE TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?;";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getTenant());
            statement.setString(2, environment);
            statement.setString(3, user.getCustomerId());
            try (ResultSet resultSet = statement.executeQuery()) {
                AppAlertConfig alertConfig;
                List<AppAlertConfig> configs = new ArrayList<>();
                while (resultSet.next()) {
                    alertConfig = new AppAlertConfig();
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
            log.error("Error getting all subscription alert config.", e);
            throw new AlertDAOException("Error getting all subscription alert config.", e);
        }
    }

    public AlertSubscription subscribeAlert(JWTUserDetails user, AlertSubscriptionInput config, String environment)
            throws AlertDAOException {
        String query = "begin tran\n"
                + "if exists (select * from API_ALERT_SUBSCRIPTION with (updlock,serializable) WHERE USER_ID = ? and "
                + "TENANT = ? and DEPLOYMENT_ID = ? and CUSTOMER_ID = ?)\n" + "begin\n"
                + "   UPDATE API_ALERT_SUBSCRIPTION SET USER_ID = ?, ALERT_TYPES = ?, EMAILS = ?, TENANT = ?, "
                + "DEPLOYMENT_ID = ?, CUSTOMER_ID = ? WHERE USER_ID = ? and TENANT = ? and DEPLOYMENT_ID = ? and "
                + "CUSTOMER_ID = ?;\n" + "end\n" + "else\n" + "begin\n"
                + "   INSERT INTO API_ALERT_SUBSCRIPTION (USER_ID, ALERT_TYPES, EMAILS, TENANT, DEPLOYMENT_ID, "
                + "CUSTOMER_ID)\n"
                + "VALUES (?, ?, ?, ?, ?, ?);\n" + "end\n" + "commit tran";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            int i = 1;
            statement.setString(i++, user.getUsername());
            statement.setString(i++, user.getTenant());
            statement.setString(i++, environment);
            statement.setString(i++, user.getCustomerId());

            statement.setString(i++, user.getUsername());
            statement.setString(i++, config.getAlertTypes());
            statement.setString(i++, config.getEmails());
            statement.setString(i++, user.getTenant());
            statement.setString(i++, environment);
            statement.setString(i++, user.getCustomerId());

            statement.setString(i++, user.getUsername());
            statement.setString(i++, user.getTenant());
            statement.setString(i++, environment);
            statement.setString(i++, user.getCustomerId());

            statement.setString(i++, user.getUsername());
            statement.setString(i++, config.getAlertTypes());
            statement.setString(i++, config.getEmails());
            statement.setString(i++, user.getTenant());
            statement.setString(i++, environment);
            statement.setString(i, user.getCustomerId());

            statement.execute();
        } catch (SQLException e) {
            log.error("Error subscribing to alert.", e);
            throw new AlertDAOException("Error subscribing to alert.", e);
        }

        return getAlertSubscription(user, environment);
    }

    public AlertSubscription unSubscribeAlert(JWTUserDetails user, String environment) throws AlertDAOException {
        String query =
                "delete from API_ALERT_SUBSCRIPTION where USER_ID = ? and TENANT = ? and DEPLOYMENT_ID = ? and "
                        + "CUSTOMER_ID = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getTenant());
            statement.setString(3, environment);
            statement.setString(4, user.getCustomerId());
            statement.execute();
        } catch (SQLException e) {
            log.error("Error un-subscribing to alert.", e);
            throw new AlertDAOException("Error un-subscribing to alert.", e);
        }
        return getAlertSubscription(user, environment);
    }

    public AlertSubscription getAlertSubscription(JWTUserDetails user, String environment) throws AlertDAOException {
        String query =
                "select USER_ID, ALERT_TYPES, EMAILS from API_ALERT_SUBSCRIPTION WHERE USER_ID = ? and TENANT = ? and"
                        + " DEPLOYMENT_ID = ? and CUSTOMER_ID = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getTenant());
            statement.setString(3, environment);
            statement.setString(4, user.getCustomerId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    AlertSubscription alertConfig = new AlertSubscription();
                    alertConfig.setAlertTypes(resultSet.getString(2));
                    alertConfig.setEmails(resultSet.getString(3));
                    return alertConfig;
                }
            }
        } catch (SQLException e) {
            log.error("Error getting alert subscription.", e);
            throw new AlertDAOException("Error getting alert subscription.", e);
        }
        return null;
    }
}

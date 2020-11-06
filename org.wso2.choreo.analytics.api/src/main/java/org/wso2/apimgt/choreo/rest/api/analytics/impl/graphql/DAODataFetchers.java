package org.wso2.apimgt.choreo.rest.api.analytics.impl.graphql;

import graphql.schema.DataFetcher;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.Utils;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.alert.AlertDAO;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.dto.APICreatorAlertConfig;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.dto.AlertSubscription;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.dto.SubscriberAlertConfig;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.interceptor.AuthenticationContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DAODataFetchers {

    public DataFetcher updateAPICreatorAlertConfig() {
        return dataFetchingEnvironment -> {
            AlertDAO dao = AlertDAO.getInstance();
            Map<String, Object> map = dataFetchingEnvironment.getArgument("alertConfig");
            AuthenticationContext context = dataFetchingEnvironment.getContext();
            APICreatorAlertConfig config = Utils.APICreatorAlertConfigFromMap(map);
            return dao.updateAPICreatorAlertConfig(context, config);
        };
    }

    public DataFetcher getAllAPICreatorAlertConfig() {
        return dataFetchingEnvironment -> {
            AlertDAO dao = AlertDAO.getInstance();
            AuthenticationContext context = dataFetchingEnvironment.getContext();
            List<APICreatorAlertConfig> list = dao.getAllAPICreatorAlertConfig(context);
            return list;
        };
    }

    public DataFetcher updateSubscriberAlertConfig() {
        return dataFetchingEnvironment -> {
            AlertDAO dao = AlertDAO.getInstance();
            Map<String, Object> map = dataFetchingEnvironment.getArgument("alertConfig");
            AuthenticationContext context = dataFetchingEnvironment.getContext();
            SubscriberAlertConfig config = Utils.SubscriberAlertConfigFromMap(map);
            return dao.updateSubscriberAlertConfig(context, config);
        };
    }

    public DataFetcher getAllSubscriberAlertConfig() {
        return dataFetchingEnvironment -> {
            AuthenticationContext context = dataFetchingEnvironment.getContext();
            AlertDAO dao = AlertDAO.getInstance();
            List<SubscriberAlertConfig> list = dao.getAllSubscriberAlertConfig(context);
            if(list == null) {
                return Collections.emptyList();
            }
            return list;
        };
    }

    public DataFetcher getAlertSubscription() {
        return dataFetchingEnvironment -> {
            AuthenticationContext context = dataFetchingEnvironment.getContext();
            String userID = dataFetchingEnvironment.getArgument("userId");
            AlertDAO dao = AlertDAO.getInstance();
            AlertSubscription subscription = dao.getAlertSubscription(context, userID);
            if(subscription == null) {
                return Collections.emptyList();
            }
            return subscription;
        };
    }

    public DataFetcher subscribeAlert() {
        return dataFetchingEnvironment -> {
            AuthenticationContext context = dataFetchingEnvironment.getContext();
            Map<String, Object> map = dataFetchingEnvironment.getArgument("config");
            AlertSubscription subscription = Utils.AlertSubscriptionFromMap(map);
            AlertDAO dao = AlertDAO.getInstance();
            AlertSubscription subscription1 = dao.subscribeAlert(context, subscription);
            return subscription1;
        };
    }

    public DataFetcher unSubscribeAlert() {
        return dataFetchingEnvironment -> {
            AuthenticationContext context = dataFetchingEnvironment.getContext();
            String userID = dataFetchingEnvironment.getArgument("userId");
            AlertDAO dao = AlertDAO.getInstance();
            AlertSubscription subscription = dao.unSubscribeAlert(context, userID);
            return subscription;
        };
    }

}

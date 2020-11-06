package org.wso2.apimgt.choreo.rest.api.analytics.impl.graphql;

import graphql.schema.DataFetcher;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.interceptor.AuthenticationContext;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.kusto.KustoQuery;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GraphQLDataFetchers {

    private static KustoQuery kustoQuery = new KustoQuery();

    public DataFetcher getApiLatencySummary() {
        return dataFetchingEnvironment -> {
//            if(true) {
//                return Collections.emptyList();
//            }
            AuthenticationContext context = dataFetchingEnvironment.getContext();
            String from = dataFetchingEnvironment.getArgument("from");
            String to = dataFetchingEnvironment.getArgument("to");
            int limit = dataFetchingEnvironment.getArgument("limit");
            String orderBy = dataFetchingEnvironment.getArgument("orderBy");
            boolean asc = dataFetchingEnvironment.getArgument("asc");

            List<String> allowedSelects = Arrays.asList("apiCreatorTenantDomain", "apiName", "apiVersion",
                    "apiResourceTemplate", "apiMethod", "avgResponseLatency", "avgServiceLatency", "avgBackendLatency",
                    "avgRequestMediationLatency", "avgResponseMediationLatency", "avgSecurityLatency", "avgThrottlingLatency",
                    "avgOtherLatency");
            List<String> possibleGroups = Arrays.asList("apiName","apiVersion","apiResourceTemplate","apiMethod");
            List<String> filters = new ArrayList<>();
            List<String> selects = dataFetchingEnvironment.getSelectionSet().getFields()
                    .stream().map(item -> item.getName())
                    .filter(item -> allowedSelects.contains(item))
                    .collect(Collectors.toList());
            List<String> groupBy = selects
                    .stream().filter(item -> possibleGroups.contains(item))
                    .collect(Collectors.toList());

            if(dataFetchingEnvironment.getArguments().containsKey("apiName")) {
                String apiName = dataFetchingEnvironment.getArgument("apiName");
                filters.add("apiName== '" + apiName + "'");
            }
            if(dataFetchingEnvironment.getArguments().containsKey("apiVersion")) {
                String apiVersion = dataFetchingEnvironment.getArgument("apiVersion");
                filters.add("apiVersion== '" + apiVersion + "'");
            }
            if(dataFetchingEnvironment.getArguments().containsKey("apiResourceTemplate")) {
                String apiResourceTemplate = dataFetchingEnvironment.getArgument("apiResourceTemplate");
                filters.add("apiResourceTemplate== '" + apiResourceTemplate + "'");
            }
            if(dataFetchingEnvironment.getArguments().containsKey("apiMethod")) {
                String apiMethod = dataFetchingEnvironment.getArgument("apiMethod");
                filters.add("apiMethod== '" + apiMethod + "'");
            }

            String formattedFrom = OffsetDateTime.parse(from).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String formattedTo = OffsetDateTime.parse(to).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            String tenant = context.getTenant();
            String orgID = "1";
            return kustoQuery.getLatencyData(formattedFrom, formattedTo, limit, orderBy, asc, tenant, orgID, selects,
                    filters, groupBy);
        };
    }
}

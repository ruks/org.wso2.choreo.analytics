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

package org.wso2.apimgt.choreo.rest.api.analytics.impl.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.apache.commons.io.IOUtils;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.config.APIConfiguration;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.interceptor.AuthenticationContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

public class GraphQLProvider {
    private GraphQL graphQL;
    private static GraphQLProvider instance;
    private APIConfiguration apiConfiguration;

    static {
        try {
            instance = new GraphQLProvider();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GraphQLProvider() throws IOException {
        String schemaFilePath = "schema.graphql";
        InputStream schemaStream = getClass().getClassLoader().getResourceAsStream(schemaFilePath);
        String schema = IOUtils.toString(schemaStream, Charset.defaultCharset());

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        GraphQLDataFetchers graphQLDataFetchers = new GraphQLDataFetchers();
        DAODataFetchers daoDataFetchers = new DAODataFetchers();
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("apiLatencySummary", graphQLDataFetchers.getApiLatencySummary())
                        .dataFetcher("getAllAPIAlertConfig", daoDataFetchers.getAllAPICreatorAlertConfig())
                        .dataFetcher("getAllAppAlertConfig", daoDataFetchers.getAllSubscriberAlertConfig())
                        .dataFetcher("getAlertSubscription", daoDataFetchers.getAlertSubscription()))
                .type(newTypeWiring("Mutation")
                        .dataFetcher("addAPIAlertConfig", daoDataFetchers.updateAPICreatorAlertConfig())
                        .dataFetcher("addAppAlertConfig", daoDataFetchers.updateSubscriberAlertConfig())
                        .dataFetcher("SubscribeAlert", daoDataFetchers.subscribeAlert())
                        .dataFetcher("UnSubscribeAlert", daoDataFetchers.unSubscribeAlert()))
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    public static GraphQLProvider getInstance() {
        return instance;
    }

    public ExecutionResult execute(AuthenticationContext context, String query, Map<String, Object> variables) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .variables(variables)
                .context(context)
                .build();
        ExecutionResult executionResult = graphQL.execute(executionInput);
        return executionResult;
    }

    public APIConfiguration getApiConfiguration() {
        return apiConfiguration;
    }

    public void setApiConfiguration(APIConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
    }
}

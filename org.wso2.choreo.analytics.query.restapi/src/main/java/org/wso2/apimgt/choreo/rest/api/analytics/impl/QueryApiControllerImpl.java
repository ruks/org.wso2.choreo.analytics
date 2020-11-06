package org.wso2.apimgt.choreo.rest.api.analytics.impl;

import graphql.ExecutionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.config.Configuration;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.graphql.GraphQLProvider;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.interceptor.AuthenticationContext;
import org.wso2.apimgt.choreo.rest.api.analytics.v1.QueryApiController;
import org.wso2.apimgt.choreo.rest.api.analytics.v1.dto.InputQueryDTO;
import org.wso2.apimgt.choreo.rest.api.analytics.v1.dto.ResultDTO;

import javax.validation.Valid;

@RestController
public class QueryApiControllerImpl extends QueryApiController {

    @Autowired
    private Configuration configuration;

    @Override
    public ResponseEntity<ResultDTO> queryPost(String environment, @Valid InputQueryDTO body) {
        GraphQLProvider provider = GraphQLProvider.getInstance();

        AuthenticationContext context = (AuthenticationContext) getRequest().get().getAttribute("authContext");
        ExecutionResult result = provider.execute(context, body.getQuery());

        ResultDTO dto = new ResultDTO();
        dto.setData(result.getData());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}

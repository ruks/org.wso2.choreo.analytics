test1
# How to run
1. update required credentials of Azure and ADX in src/main/resources/application.yml
2. build the project with `mvn clean install`
3. run it with, `java -jar target/org.wso2.choreo.analytics.api-<project.version>>.jar --spring.config.location=target/application.yml`
4. run with env variables
```
    java -jar target/org.wso2.choreo.analytics.api-0.1-SNAPSHOT.jar \
    --datasource.username="admin" \
    --datasource.password="admin" \
    --datasource.url="jdbc:sqlserver://localhost:1433;database=analytics" \
    --kusto.applicationClientId="34c4062d-3588-4426-a6e1-de1fc88fb8a3" \
    --kusto.applicationKey="9cf4fdb3-ec1f-406a-808e-acd33c9e390a" \
    --kusto.authorityId="2b367c99-e2d6-4259-8cc7-d62a110d678f" \
    --kusto.resourceUri="https://localhost:9099/query" \
    --kusto.database="analytics" \
    --security.jwks="https://localhost:9443/oauth2/jwks" \
    --security.jwksIssuer="https://localhost:9443/oauth2/token"
```    
5. run with env variables and different logging level
```
    java -jar target/org.wso2.choreo.analytics.api-0.1-SNAPSHOT.jar \
    --datasource.username="admin" \
    --datasource.password="admin" \
    --datasource.url="jdbc:sqlserver://localhost:1433;database=analytics" \
    --kusto.applicationClientId="34c4062d-3588-4426-a6e1-de1fc88fb8a3" \
    --kusto.applicationKey="9cf4fdb3-ec1f-406a-808e-acd33c9e390a" \
    --kusto.authorityId="2b367c99-e2d6-4259-8cc7-d62a110d678f" \
    --kusto.resourceUri="https://localhost:9099/query" \
    --kusto.database="analytics" \
    --security.jwks="https://localhost:9443/oauth2/jwks" \
    --security.jwksIssuer="https://localhost:9443/oauth2/token" \
    --logging.level.org.wso2.choreo.analytics.api=DEBUG
```  


# How to test
1. API is served in url `http://localhost:8080/graphql`
2. Take access token from choreo IDP and use it in `Authorization` header
3. Execute graphQL operations

ex:
1. Util Operations
    - Query 
    ```
    query($environment: Environment!) {
    
        listAllAPI(environment: $environment) {
            id
            name
            version
            provider
        }
    
        listApplications(environment: $environment) {
            id
            name
            owner
        }
    
        listProviders(environment: $environment) {
            name
        }
    }   
    ```
    
    - Variables:
    ```
    {
        "environment": {
            "name" : "prod"
        }
    }
    ```


1. Overview Operations:
    - Query
    ```
    query($environment: Environment!, $filter: TimeFilter!) {
    
        getTotalTraffic(filter: $filter, environment: $environment)
        getTotalProxyErrors(filter: $filter, environment: $environment)
        getTotalTargetErrors(filter: $filter, environment: $environment)
        getOverallLatency(filter: $filter, environment: $environment)
        getLatencySummary(filter: $filter, environment: $environment) {
            timeSpan
            latencyTime
        }
        getSuccessSummary(filter: $filter, environment: $environment) {
            timeSpan
            requestCount
        }
        getTargetErrorSummary(filter: $filter, environment: $environment) {
            timeSpan
            errorCount
        }
        getProxyErrorSummary(filter: $filter, environment: $environment) {
            timeSpan
            errorCount
        }
    }   
    ```
  
    - Variables:
    ```
    {
      "filter": {
        "from": "2020-10-10T14:43:16.023+05:30",
        "to": "2020-11-24T16:43:16.023+05:30"
      },
      "environment": {
            "name" : "prod"
      }
    }
    ```

1. List Alert configurations and Subscription
    - Query
    ```
    query($environment: Environment!) {
    
        getAllAPIAlertConfig(environment: $environment) {
            apiName
            apiVersion
            thresholdResponseTime
            thresholdBackendTime
        }
        getAllAppAlertConfig(environment: $environment) {
            apiName
            apiVersion
            appName
            appOwner
            thresholdRequestCount
        }
        getAlertSubscription(environment: $environment) {
            alertTypes
            emails
        }
    }
    ```

    - Variables
    ```
    {
      "environment": {
            "name" : "prod"
      }
    }
    ```

1. Add Alert configurations and Subscription
    - Query
    ```
    mutation($apiAlertConfig:APIAlertConfigInput, $appAlertConfig:AppAlertConfigInput, $subsConfig:AlertSubscriptionInput, $environment: Environment!) {
    
        addAPIAlertConfig(alertConfig:$apiAlertConfig environment: $environment) {
            apiName
            apiVersion
            thresholdResponseTime
            thresholdBackendTime
        }
        
        addAppAlertConfig(alertConfig:$appAlertConfig environment: $environment) {
            apiName
            apiVersion
            appName
            appOwner
            thresholdRequestCount
        }
    
        subscribeAlert(config:$subsConfig environment: $environment) {
            alertTypes
            emails
        }
    }
    ```
    
    - Variables:
    ```
    {
        "apiAlertConfig": {
            "apiName": "orderAPI",
            "apiVersion": "1.0.0",
            "thresholdResponseTime": 200,
            "thresholdBackendTime": 100
        },
        "appAlertConfig": {
            "apiName": "orderAPI",
            "apiVersion": "1.0.0",
            "appName": "orderApp",
            "appOwner": "admin",
            "thresholdRequestCount": 200
        },
        "subsConfig" : {
            "alertTypes":"tier_limit",
            "emails":"rukshan@wso2.com"
        },
        "environment": {
            "name" : "prod"
        }
    }
    ```

1. Un-subscribe alerts
    - Query
    ```
    mutation($environment: Environment!) {
    
        unSubscribeAlert(environment: $environment) {
            alertTypes
            emails
        }
    }
    ```
    
    - Variables:
    ```
    {
      "environment": {
            "name" : "prod"
      }
    }
    ```

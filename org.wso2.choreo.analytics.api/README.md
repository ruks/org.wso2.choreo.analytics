# How to run
1. build the project with `mvn clean install`
2. run it with, `java -jar target/org.wso2.apimgt.choreo.api-0.1-SNAPSHOT.jar`
3. update required credentials of Azure and ADX in org.wso2.choreo.analytics.gql.kusto.properties

# How to test
1. Connect to the WS `ws://localhost:9010/choreo/api/analytics`
2. Send graphQL queries
```
{
  apiLatencySummary(from: "2020-10-10T14:43:16.023+05:30", to: "2020-10-22T14:43:16.023+05:30", limit: 10, orderBy: "apiName", asc: false) {
    id
    apiName
    apiVersion
    apiResourceTemplate
    apiMethod
    avgResponseLatency
    avgServiceLatency
    avgBackendLatency
    avgRequestMediationLatency
    avgResponseMediationLatency
    avgSecurityLatency
    avgThrottlingLatency
    avgOtherLatency
  }
}

```

# How to run
1. update required credentials of Azure and ADX in src/main/resources/application.yml
2. build the project with `mvn clean install`
3. run it with, `java -jar target/org.wso2.choreo.analytics.api-<project.version>>.jar`


# How to test
1. API is served in url `http://localhost:8080/graphql`
2. Take access token from choreo IDP and use it in `Authorization` header
3. Execute graphQL operations

ex:
- Util Operations 
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

Variables:
```
{
    "environment": {
        "name" : "prod"
    }
}
```


- Overview Operations:
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

Variables:
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

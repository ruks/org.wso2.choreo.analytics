FROM openjdk:12-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/org.wso2.choreo.analytics.gql-0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
#docker build -t wso2choreo/analytics-api .
#docker run -p 8080:8080 wso2choreo/analytics-api -d

#docker run -d --name analyticsApi -p 8080:8080 wso2choreo/analytics-api
#docker container logs -f analyticsApi
#docker ps -a
#docker rm -f analyticsApi

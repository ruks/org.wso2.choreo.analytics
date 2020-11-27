FROM openjdk:12-jdk-alpine
ARG VERSION
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=org.wso2.choreo.analytics.api-$VERSION.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

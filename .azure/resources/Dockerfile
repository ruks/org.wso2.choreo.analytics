FROM openjdk:12-jdk-alpine
ARG VERSION
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=analytics.api.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

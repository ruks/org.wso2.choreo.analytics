FROM adoptopenjdk/openjdk11:jre-11.0.9_11-alpine
ARG VERSION
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=analytics.api.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

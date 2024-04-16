FROM openjdk:21-jdk-oracle

WORKDIR /app
COPY target/management_backend_v1.jar /tmp/management_backend_v1.jar
COPY .env /app/.env
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/tmp/management_backend_v1.jar"]
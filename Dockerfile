FROM openjdk:11.0.12-jdk
COPY build/libs/doc-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
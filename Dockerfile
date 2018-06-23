FROM openjdk:8-alpine

COPY docker/application/files/application.properties /opt/vanhackathon/application/

COPY target/vanhackathon-1.0-SNAPSHOT.jar /opt/vanhackathon/application/

WORKDIR /opt/vanhackathon/application/
ENTRYPOINT ["java", "-jar", "vanhackathon-1.0-SNAPSHOT.jar"]

EXPOSE 8080
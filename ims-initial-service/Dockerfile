FROM openjdk:8
LABEL maintainer="bel.co.in"
COPY target/ims-initial-service-0.0.1-SNAPSHOT.jar ims-service-docker-image.jar
ENTRYPOINT ["java", "-jar", "ims-service-docker-image.jar"]
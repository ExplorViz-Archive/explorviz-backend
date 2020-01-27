FROM adoptopenjdk/openjdk11:alpine-jre

RUN adduser -D -H explorviz

WORKDIR /opt/explorviz
COPY build/libs/explorviz-discovery-service.jar .

USER explorviz
CMD ["java", "-jar", "explorviz-discovery-service.jar"]

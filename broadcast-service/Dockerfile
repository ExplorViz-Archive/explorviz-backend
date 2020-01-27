FROM adoptopenjdk/openjdk11:alpine-jre

RUN adduser -D -H explorviz

WORKDIR /opt/explorviz
COPY build/libs/explorviz-broadcast-service.jar .

USER explorviz
CMD ["java", "-jar", "explorviz-broadcast-service.jar"]

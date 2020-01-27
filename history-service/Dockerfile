FROM adoptopenjdk/openjdk11:alpine-jre

RUN adduser -D -H explorviz

WORKDIR /opt/explorviz
COPY build/libs/explorviz-history-service.jar .

USER explorviz
CMD ["java", "-jar", "explorviz-history-service.jar"]

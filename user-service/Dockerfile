FROM adoptopenjdk/openjdk11:alpine-jre

RUN adduser -D -H explorviz

ENV SERVICES_SETTINGS settings-service:8087

WORKDIR /opt/explorviz
COPY build/libs/explorviz-user-service.jar .

USER explorviz
CMD ["java", "-jar", "explorviz-user-service.jar"]

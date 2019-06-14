FROM openjdk:8-alpine

ENV SERVICES_SETTINGS settings-service:8087

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-user-service.jar .

CMD java -jar explorviz-user-service.jar
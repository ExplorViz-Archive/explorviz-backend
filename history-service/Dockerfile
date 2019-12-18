FROM openjdk:11-jre-slim

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-history-service.jar .

CMD java -jar explorviz-history-service.jar
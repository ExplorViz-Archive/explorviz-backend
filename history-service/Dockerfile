FROM openjdk:8-alpine

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-history-service.jar .

CMD java -jar explorviz-history-service.jar
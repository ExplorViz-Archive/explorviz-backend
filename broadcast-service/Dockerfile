FROM openjdk:8-alpine

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-broadcast-service.jar .

CMD java -jar explorviz-broadcast-service.jar
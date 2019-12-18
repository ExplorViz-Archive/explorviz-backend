FROM openjdk:11-jre-slim

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-discovery-service.jar .

CMD java -jar explorviz-discovery-service.jar
FROM openjdk:11-jre-slim

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-landscape-service.jar .

CMD java -jar explorviz-landscape-service.jar
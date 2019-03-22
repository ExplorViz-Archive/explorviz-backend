FROM openjdk:8-alpine

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-discovery.jar .

CMD java -jar explorviz-discovery.jar
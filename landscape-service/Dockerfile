FROM adoptopenjdk/openjdk11:jdk-11.0.4_11-alpine-slim

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-landscape-service.jar .

CMD java -jar explorviz-landscape-service.jar
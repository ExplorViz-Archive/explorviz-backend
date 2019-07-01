FROM openjdk:8-alpine

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-landscape-service.jar .

CMD java -jar explorviz-landscape-service.jar
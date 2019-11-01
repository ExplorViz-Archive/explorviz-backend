FROM openjdk:11

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-history-service.jar .

CMD java -jar explorviz-history-service.jar
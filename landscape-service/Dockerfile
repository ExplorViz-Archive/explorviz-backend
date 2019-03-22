FROM openjdk:8-alpine

ENV DUMMY_MODE false
ENV MONGO_HOST 127.0.0.1
ENV MONGO_PORT 27018
ENV REDIS_HOST 127.0.0.1

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-landscape-service.jar .
RUN mkdir META-INF
COPY build/resources/main/explorviz.properties META-INF/explorviz-custom.properties

COPY prod-env-updater.sh .
RUN chmod +x ./prod-env-updater.sh

CMD ./prod-env-updater.sh &&  java -cp explorviz-landscape-service.jar:META-INF net.explorviz.landscape.server.main.Main
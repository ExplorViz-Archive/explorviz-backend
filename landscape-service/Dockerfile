FROM openjdk:8-alpine

ENV SERVICE_PREFIX landscape

ENV DUMMY_MODE false

ENV MONGO_HOST 127.0.0.1
ENV MONGO_PORT 27018

ENV REDIS_ID_GENERATOR false
ENV REDIS_HOST 127.0.0.1

ENV KAFKA_BOOTSTRAP_SERVERS localhost:9092
ENV KAFKA_TOPIC_NAME landscape-update

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-landscape-service.jar .
RUN mkdir META-INF
COPY build/resources/main/explorviz.properties META-INF/explorviz-custom.properties

COPY prod-env-updater.sh .
RUN chmod +x ./prod-env-updater.sh

CMD ./prod-env-updater.sh &&  java -cp explorviz-landscape-service.jar:META-INF net.explorviz.landscape.server.main.Main
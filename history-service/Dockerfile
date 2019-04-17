FROM openjdk:8-alpine

ENV SERVICE_PREFIX history

ENV MONGO_HOST 127.0.0.1
ENV MONGO_PORT 27018
ENV MONGO_DB_NAME explorviz

ENV REDIS_ID_GENERATOR false
ENV REDIS_HOST 127.0.0.1

ENV KAFKA_BOOTSTRAP_SERVERS localhost:9092
ENV KAFKA_TOPIC_NAME landscape-update
ENV KAFKA_GROUP_ID history-1

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-history-service.jar .
RUN mkdir META-INF
COPY build/resources/main/explorviz.properties META-INF/explorviz-custom.properties

COPY prod-env-updater.sh .
RUN chmod +x ./prod-env-updater.sh

CMD ./prod-env-updater.sh &&  java -cp explorviz-history-service.jar:META-INF net.explorviz.history.server.main.Main
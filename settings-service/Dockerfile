FROM openjdk:11-jre-slim

ENV SERVICE_PREFIX settings

ENV MONGO_HOST 127.0.0.1
ENV MONGO_PORT 27019

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-settings-service.jar .
RUN mkdir META-INF
COPY build/resources/main/explorviz.properties META-INF/explorviz-custom.properties

COPY prod-env-updater.sh .
RUN chmod +x ./prod-env-updater.sh

CMD ./prod-env-updater.sh && java -cp explorviz-settings-service.jar:META-INF net.explorviz.settings.server.main.Main
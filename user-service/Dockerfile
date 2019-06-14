FROM openjdk:8-alpine

ENV MONGO_HOST 127.0.0.1
ENV MONGO_PORT 27017
ENV REDIS_HOST 127.0.0.1
ENV SERVICE_PREFIX auth
ENV SERVICES_SETTINGS localhost:8087

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-user-service.jar .
RUN mkdir META-INF
COPY build/resources/main/explorviz.properties META-INF/explorviz-custom.properties

COPY prod-env-updater.sh .
RUN chmod +x ./prod-env-updater.sh

CMD ./prod-env-updater.sh && java -cp explorviz-user-service.jar:META-INF net.explorviz.security.server.main.Main
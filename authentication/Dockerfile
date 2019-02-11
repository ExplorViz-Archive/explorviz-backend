FROM openjdk:8-alpine

ENV MONGO_IP 127.0.0.1

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-authentication.jar .
RUN mkdir META-INF
COPY build/resources/main/explorviz.properties META-INF/explorviz-custom.properties

COPY prod-env-updater.sh .
RUN chmod +x ./prod-env-updater.sh

CMD ./prod-env-updater.sh && java -cp explorviz-authentication.jar:META-INF net.explorviz.security.server.main.Main
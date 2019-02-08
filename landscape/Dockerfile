FROM openjdk:8-alpine

ENV DUMMY_MODE false

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-landscape.jar .
RUN mkdir META-INF
COPY build/resources/main/explorviz.properties META-INF/explorviz-custom.properties

COPY prod-env-updater.sh .
RUN chmod +x ./prod-env-updater.sh

CMD ./prod-env-updater.sh &&  java -cp explorviz-landscape.jar:META-INF net.explorviz.landscape.server.main.Main
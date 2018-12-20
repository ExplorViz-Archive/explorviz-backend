FROM openjdk:8-alpine

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-analysis.jar .
#RUN mkdir META-INF
#COPY build/resources/main/explorviz.properties META-INF/explorviz-custom.properties

#COPY prod-env-updater.sh .
#RUN chmod +x ./prod-env-updater.sh

CMD java -jar explorviz-analysis.jar

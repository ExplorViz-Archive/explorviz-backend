FROM openjdk:8-alpine

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-analysis.jar .

# workaround due to oly analysis properties
RUN jar xf explorviz-analysis.jar
COPY explorviz.docker.live_trace_processing.properties META-INF/explorviz.live_trace_processing.properties

#COPY prod-env-updater.sh .
#RUN chmod +x ./prod-env-updater.sh

#CMD java -jar explorviz-analysis.jar

#CMD java -cp explorviz-analysis.jar:META-INF net.explorviz.analysis.Main
CMD java -cp . net.explorviz.analysis.Main
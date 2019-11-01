FROM openjdk:11

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-analysis-service.jar .

# workaround due to old analysis properties
RUN jar xf explorviz-analysis-service.jar
COPY explorviz.docker.live_trace_processing.properties META-INF/explorviz.live_trace_processing.properties

#COPY prod-env-updater.sh .
#RUN chmod +x ./prod-env-updater.sh

#CMD java -jar explorviz-analysis-service.jar

#CMD java -cp explorviz-analysis-service.jar:META-INF net.explorviz.analysis.Main
CMD java -cp . net.explorviz.analysis.Main
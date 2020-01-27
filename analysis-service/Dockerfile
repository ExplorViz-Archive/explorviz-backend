FROM adoptopenjdk/openjdk11:alpine-slim AS extractor

WORKDIR /opt/explorviz
COPY build/libs/explorviz-analysis-service.jar .

# workaround due to old analysis properties
RUN jar xf explorviz-analysis-service.jar \
 && rm explorviz-analysis-service.jar

FROM adoptopenjdk/openjdk11:alpine-jre

RUN adduser -D -H explorviz

WORKDIR /opt/explorviz
COPY --from=extractor /opt/explorviz .
COPY explorviz.docker.live_trace_processing.properties META-INF/explorviz.live_trace_processing.properties

USER explorviz
#CMD ["java", "-jar", "explorviz-analysis-service.jar"]
#CMD ["java", "-cp", "explorviz-analysis-service.jar:META-INF", "net.explorviz.analysis.Main"]
CMD ["java", "-cp", ".", "net.explorviz.analysis.Main"]

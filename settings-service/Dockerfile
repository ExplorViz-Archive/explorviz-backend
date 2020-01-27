FROM adoptopenjdk/openjdk11:alpine-jre

RUN adduser -D -H explorviz

ENV SERVICE_PREFIX settings
ENV MONGO_HOST 127.0.0.1
ENV MONGO_PORT 27019

WORKDIR /opt/explorviz
COPY build/libs/explorviz-settings-service.jar .
COPY --chown=explorviz:explorviz build/resources/main/explorviz.properties META-INF/explorviz-custom.properties

COPY entrypoint.sh .
COPY prod-env-updater.sh .

USER explorviz
ENTRYPOINT ["./entrypoint.sh"]
CMD ["java", "-cp", "explorviz-settings-service.jar:META-INF", "net.explorviz.settings.server.main.Main"]

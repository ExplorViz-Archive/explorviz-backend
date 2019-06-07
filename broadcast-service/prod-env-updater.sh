sed -i "s#service.prefix=.*#service.prefix=$SERVICE_PREFIX#g" META-INF/explorviz-custom.properties

sed -i "s#redis.host=.*#redis.host=$REDIS_HOST#g" META-INF/explorviz-custom.properties

sed -i "s#exchange.kafka.bootstrap.servers=.*#exchange.kafka.bootstrap.servers=$KAFKA_BOOTSTRAP_SERVERS#g" META-INF/explorviz-custom.properties

sed -i "s#exchange.kafka.topic.name=.*#exchange.kafka.topic.name=$KAFKA_TOPIC_NAME#g" META-INF/explorviz-custom.properties

sed -i "s#exchange.kafka.group.id=.*#exchange.kafka.group.id=$KAFKA_GROUP_ID#g" META-INF/explorviz-custom.properties
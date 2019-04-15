sed -i "s#service.prefix=.*#service.prefix=$SERVICE_PREFIX#g" META-INF/explorviz-custom.properties

sed -i "s#repository.useDummyMode=.*#repository.useDummyMode=$DUMMY_MODE#g" META-INF/explorviz-custom.properties

sed -i "s#mongo.host=.*#mongo.host=$MONGO_HOST#g" META-INF/explorviz-custom.properties

sed -i "s#mongo.port=.*#mongo.port=$MONGO_PORT#g" META-INF/explorviz-custom.properties

sed -i "s#redis.host=.*#redis.host=$REDIS_HOST#g" META-INF/explorviz-custom.properties

sed -i "s#exchange.kafka.bootstrap.servers=.*#exchange.kafka.bootstrap.servers=$KAFKA_BOOTSTRAP_SERVERS#g" META-INF/explorviz-custom.properties

sed -i "s#exchange.kafka.topic.name=.*#exchange.kafka.topic.name=$KAFKA_TOPIC_NAME#g" META-INF/explorviz-custom.properties
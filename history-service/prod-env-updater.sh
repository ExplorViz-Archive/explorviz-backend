sed -i "s#service.prefix=.*#service.prefix=$SERVICE_PREFIX#g" META-INF/explorviz-custom.properties

sed -i "s#mongo.host=.*#mongo.host=$MONGO_HOST#g" META-INF/explorviz-custom.properties

sed -i "s#mongo.db=.*#mongo.db=$MONGO_DB_NAME#g" META-INF/explorviz-custom.properties

sed -i "s#exchange.kafka.topic.name=.*#exchange.kafka.topic.name=$KAFKA_GROUP_ID#g" META-INF/explorviz-custom.properties

sed -i "s#redis.host=.*#redis.host=$REDIS_HOST#g" META-INF/explorviz-custom.properties

sed -i "s#exchange.kafka.bootstrap.servers=.*#exchange.kafka.bootstrap.servers=$KAFKA_BOOTSTRAP_SERVERS#g" META-INF/explorviz-custom.properties

sed -i "s#exchange.kafka.topic.name=.*#exchange.kafka.topic.name=$KAFKA_TOPIC_NAME#g" META-INF/explorviz-custom.properties

sed -i "s#exchange.kafka.topic.name=.*#exchange.kafka.topic.name=$KAFKA_GROUP_ID#g" META-INF/explorviz-custom.properties
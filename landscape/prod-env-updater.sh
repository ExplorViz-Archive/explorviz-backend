# sed -i "s#mongo.ip=.*#mongo.ip=$MONGO_IP#g" META-INF/explorviz-custom.properties

sed -i "s#repository.useDummyMode=.*#repository.useDummyMode=$DUMMY_MODE#g" META-INF/explorviz-custom.properties
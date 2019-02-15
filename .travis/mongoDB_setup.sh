#!/bin/bash
echo -e "Configuring mongoDbs...\n"

wget http://fastdl.mongodb.org/linux/mongodb-linux-x86_64-$MONGODB_VERSION.tgz
tar xfz mongodb-linux-x86_64-$MONGODB_VERSION.tgz
export PATH=`pwd`/mongodb-linux-x86_64-$MONGODB_VERSION/bin:$PATH
mkdir -p data/mongoDBAuth
mongod --dbpath=data/mongoDBAuth --port 27017 &
sleep 3
mkdir -p data/mongoDBLandscape
mongod --dbpath=data/mongoDBLandscape --port 27018 &
sleep 3

echo -e "Configured mongoDbs...\n"
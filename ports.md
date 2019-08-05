# Ports and Services

## Core

Service                     | Port      | Type  
---                         | ---       | ---
Analysis-Service            | 10133     | TCP/RAW
Broadcast-Service           | 8081      | HTTP/SSE
Discovery-Service           | 8083      | HTTP
History-Service             | 8086      | HTTP
Landscape-Service           | 10135     | TCP/RAW
Setting-Service             | 8087      | HTTP
User-Service                | 8082      | HTTP
Frontend (prod)             | 8090      | HTTP
Frontend (dev)              | 4200      | HTTP

## Software Stack

Service                     | Port      | Type
---                         | ---       | ---
User-Mongo                  | 27017     | mongodb
Settings-Service            | 27019     | mongodb      
History-Service             | 27018     | mongodb
Id-Generator                | 6379      | redis
Kafka                       | 9092      | kafka
Zookeper                    | 2181      | zookeeper  

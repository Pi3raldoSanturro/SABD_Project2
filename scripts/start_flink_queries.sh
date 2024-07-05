#!/bin/bash

docker exec -d jobmanager flink run /opt/flink/jobs/Query1-1.0.jar
sleep 5
docker exec -d jobmanager flink run /opt/flink/jobs/Query1_3_days-1.0.jar
sleep 5
docker exec -d jobmanager flink run /opt/flink/jobs/Query1_all_days-1.0.jar
sleep 5
docker exec -d jobmanager flink run /opt/flink/jobs/Query2_1_day-1.0.jar
sleep 5
docker exec -d jobmanager flink run /opt/flink/jobs/Query2_3_days-1.0.jar
sleep 5
docker exec -d jobmanager flink run /opt/flink/jobs/Query2_all_days-1.0.jar
sleep 5
docker exec -d jobmanager flink run /opt/flink/jobs/Query3-1.0.jar

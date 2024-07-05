#!/bin/bash

docker cp ./Flink_Queries/Query1_1_day/target/Query1-1.0.jar jobmanager:/opt/flink/jobs/Query1-1.0.jar
docker cp ./Flink_Queries/Query1_3_days/target/Query1_3_days-1.0.jar jobmanager:/opt/flink/jobs/Query1_3_days-1.0.jar
docker cp ./Flink_Queries/Query1_all_days/target/Query1_all_days-1.0.jar jobmanager:/opt/flink/jobs/Query1_all_days-1.0.jar
docker cp ./Flink_Queries/Query2_1_day/target/Query2_1_day-1.0.jar jobmanager:/opt/flink/jobs/Query2_1_day-1.0.jar
docker cp ./Flink_Queries/Query2_3_days/target/Query2_3_days-1.0.jar jobmanager:/opt/flink/jobs/Query2_3_days-1.0.jar
docker cp ./Flink_Queries/Query2_all_days/target/Query2_all_days-1.0.jar jobmanager:/opt/flink/jobs/Query2_all_days-1.0.jar
docker cp ./Flink_Queries/Query3/target/Query3-1.0.jar jobmanager:/opt/flink/jobs/Query3-1.0.jar








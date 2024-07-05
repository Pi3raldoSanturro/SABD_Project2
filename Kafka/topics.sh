#!/bin/bash


docker exec -it kafka-broker /usr/bin/kafka-topics --create --topic batch-dataset --bootstrap-server kafka-broker:9092 --partitions 1 --replication-factor 1


# python send_to_kafka.py

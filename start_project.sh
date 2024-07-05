#!/bin/bash

# echo "Creating a python virtual environment for dependencies"
# echo ""
# echo ""

# python3 -m venv venv
# source venv/bin/activate

# pip install panda
# pip install pandas kafka-python-ng
# pip install pandas pyarrow


echo "Insert the dataset named 'dataset.csv' file in the directory dataset"
echo "Press ENTER when you have done, the dataset will be filtered in the dataset directory"
read input

echo ""
echo ""

# Filtering dataset
python3 ./scripts/filter_dataset_csv.py

echo ""
echo ""

# Starting Docker Compose
gnome-terminal --title="Docker Compose Bash" -- bash -c "docker compose up"
echo ""
echo ""

echo "You can check if the services are up at:

Apache Flink Web Ui:    http://localhost:8081/
Kafdrop Web Ui:         http://localhost:9000/

"
echo ""
echo "If all the services are up and running you can proceed with the project. Some other bashes will be created"
echo "Press ENTER"
read input

# Mounting Flink Jobs

gnome-terminal --title="Mounting Flink Jobs bash" -- bash -c "./scripts/build_project_queries.sh; ./scripts/move_jars.sh"
echo ""
echo ""

# Kafka & Flink
echo "Now the kafka topic will be populated and after a short time, the flink jobs will run.
You can check the results in the Flink/Results/ folder. Wait until the projects jars file are completely mounted"
echo "Press ENTER"
read input
./Kafka/topics.sh
gnome-terminal --title="Kafka Topic" -- bash -c "python3 ./Kafka/send_kafka_1_0.py"
sleep 5
gnome-terminal --title="Flink Jobs" -- bash -c "./scripts/start_flink_queries.sh"

echo ""
echo ""


# Prompt to stop the project and clear all containers
echo "Press ENTER to stop the project and clear all containers"
read input

# Stopping the project and clearing all containers
echo "Just Wait..."
./scripts/remove_jars.sh
./stop_all.sh

echo ""
echo ""

#deactivate

# Project ended
#FINISHED

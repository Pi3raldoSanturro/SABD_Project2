import csv
import json
from kafka import KafkaProducer

# Configura il produttore Kafka
producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

# Specifica il file di input e il topic Kafka
input_file = './dataset/filtered_dataset.csv'
topic = 'batch-dataset'

# Leggi il file CSV e invia ogni riga a Kafka
with open(input_file, mode='r') as file:
    reader = csv.DictReader(file)
    for row in reader:
        # Invia il messaggio a Kafka
        producer.send(topic, value=row)
        print(f"Sent: {row}")

# Assicurati che tutti i messaggi siano stati inviati
producer.flush()

print("All messages sent to Kafka successfully.")

import pandas as pd
from kafka import KafkaProducer
import json
import logging

# Configurazione del logging
logging.basicConfig(level=logging.INFO)

# Configurazione del produttore Kafka
producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8'),
    max_request_size=1048576  # Ad esempio, impostare un limite di 1 MB
)

# Leggi il file Parquet
try:
    df = pd.read_parquet('../dataset/filtered_dataset.parquet')
    logging.info("Dataset letto con successo")
except Exception as e:
    logging.error(f"Errore nella lettura del dataset: {e}")
    exit(1)

# Raggruppa i dati per giorno senza convertire il formato delle date
daily_groups = df.groupby(df['date'].str[:10])

# Funzione callback di successo
def on_send_success(metadata):
    logging.info(f"Message sent to topic {metadata.topic}, partition {metadata.partition}, offset {metadata.offset}")

# Funzione callback di errore
def on_send_error(excp):
    logging.error(f"Failed to send message: {excp}")

# Funzione per inviare batch suddivisi
def send_batches_in_chunks(date, group, chunk_size=1000):
    for start in range(0, len(group), chunk_size):
        end = start + chunk_size
        batch_data = group.iloc[start:end].to_dict(orient='records')
        try:
            producer.send('batch-dataset', value=batch_data).add_callback(on_send_success).add_errback(on_send_error)
            logging.info(f"Sent batch chunk for date {date} to topic 'batch-dataset': {len(batch_data)} records")
        except Exception as e:
            logging.error(f"Failed to send batch chunk for date {date}: {e}")

# Invia i dati a Kafka in batch giornalieri suddivisi
for date, group in daily_groups:
    send_batches_in_chunks(date, group)

producer.flush()
producer.close()
logging.info("Messaggi inviati con successo")

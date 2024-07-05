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
    max_request_size=50000000,  # Aumenta la dimensione massima della richiesta a 50 MB
    request_timeout_ms=60000,   # Aumenta il timeout della richiesta a 60 secondi
    linger_ms=5000              # Attende fino a 5 secondi per inviare i messaggi
)

# Leggi il file CSV
input_file = '../dataset/filtered_dataset.csv'
try:
    df = pd.read_csv(input_file)
    logging.info("Dataset letto con successo")
except Exception as e:
    logging.error(f"Errore nella lettura del dataset: {e}")
    exit(1)

# Assicurati che la colonna 'date' sia in formato stringa
df['date'] = df['date'].astype(str)

# Rimuovi eventuali righe con date vuote
df = df[df['date'].str.strip() != '']

# Raggruppa i dati per giorno
daily_groups = df.groupby(df['date'].str[:10])

# Funzione callback di successo
def on_send_success(metadata):
    logging.info(f"Message sent to topic {metadata.topic}, partition {metadata.partition}, offset {metadata.offset}")

# Funzione callback di errore
def on_send_error(excp):
    logging.error(f"Failed to send message: {excp}")

# Invia i dati a Kafka in batch giornalieri
for date, group in daily_groups:
    batch_data = group.to_dict(orient='records')
    try:
        producer.send('batch-dataset', value={'date': date, 'entries': batch_data}).add_callback(on_send_success).add_errback(on_send_error)
        logging.info(f"Sent data for date {date} to topic 'batch-dataset': {len(batch_data)} records")
    except Exception as e:
        logging.error(f"Failed to send data for date {date}: {e}")

producer.flush()
producer.close()
logging.info("Messaggi inviati con successo")

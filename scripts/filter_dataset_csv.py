import pandas as pd

# Specifica il file di input e di output
input_file = './dataset/dataset.csv'
output_file = './dataset/filtered_dataset.csv'

# Colonne da mantenere
columns_to_keep = ['date', 'serial_number', 'model', 'failure', 'vault_id', 's9_power_on_hours', 's194_temperature_celsius']

# Carica il dataset
df = pd.read_csv(input_file, usecols=columns_to_keep, low_memory=False)

# Gestisci le conversioni dei tipi di dati
# Converte la colonna 'date' in formato datetime, eventuali errori di conversione vengono trasformati in NaT
df['date'] = pd.to_datetime(df['date'], errors='coerce')

# Converte la colonna 'serial_number' in stringa
df['serial_number'] = df['serial_number'].astype(str)

# Converte la colonna 'model' in stringa
df['model'] = df['model'].astype(str)

# Converte la colonna 'failure' in numerico, eventuali errori di conversione vengono trasformati in NaN,
# successivamente i NaN vengono riempiti con 0 e infine convertiti in interi
df['failure'] = pd.to_numeric(df['failure'], errors='coerce').fillna(0).astype(int)

# Converte la colonna 'vault_id' in numerico, eventuali errori di conversione vengono trasformati in NaN,
# successivamente i NaN vengono riempiti con 0 e infine convertiti in interi
df['vault_id'] = pd.to_numeric(df['vault_id'], errors='coerce').fillna(0).astype(int)

# Converte la colonna 's9_power_on_hours' in numerico, eventuali errori di conversione vengono trasformati in NaN,
# successivamente i NaN vengono riempiti con 0.0
df['s9_power_on_hours'] = pd.to_numeric(df['s9_power_on_hours'], errors='coerce').fillna(0.0)

# Converte la colonna 's194_temperature_celsius' in numerico, eventuali errori di conversione vengono trasformati in NaN,
# successivamente i NaN vengono riempiti con 0.0
df['s194_temperature_celsius'] = pd.to_numeric(df['s194_temperature_celsius'], errors='coerce').fillna(0.0)

# Salva il nuovo dataset filtrato in un nuovo file CSV
df.to_csv(output_file, index=False)

print(f"Il dataset filtrato è stato salvato in {output_file}")

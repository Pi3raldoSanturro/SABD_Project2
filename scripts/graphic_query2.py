import csv
import matplotlib.pyplot as plt
import os

DATE = '2023-04-22'

# Percorso del file CSV
file_path = '../Results/Query2/Query2_all_days/output_'+ DATE +'.csv'

# Inizializza le liste per latency e throughput
latency = []
throughput = []

# Leggi il file riga per riga e estrai i valori di latency e throughput
with open(file_path, 'r') as file:
    reader = csv.reader(file)
    
    # Salta la prima riga (intestazione)
    next(reader)
    
    # Leggi ogni riga del file
    for line in reader:
        # Estrai i valori di latency e throughput
        latency_value = float(line[1]) / 1000  # Converti da ms a secondi
        throughput_value = float(line[2])
        
        # Aggiungi i valori alle liste
        latency.append(latency_value)
        throughput.append(throughput_value)

# Verifica se ci sono dati da graficare
if not latency or not throughput:
    print("Non ci sono dati validi per creare il grafico.")
else:
    # Crea il grafico
    plt.figure(figsize=(20, 6))
    plt.plot(latency, marker='.', linestyle='-', color='blue', label='latency (s)')
    plt.plot(throughput, marker='.', linestyle='-', color='green', label='throughput')

    # Aggiungi titolo e etichette agli assi
    plt.title('Grafico dei valori di latency (s) e throughput per '+ DATE)
    plt.xlabel('Indice')
    plt.ylabel('Valori')

    # Aggiungi una griglia
    plt.grid(True)

    # Aggiungi una legenda
    plt.legend()

    # Directory di output
    output_dir = '../Graphics/Query2/latency_throughput/' + DATE + '/'
    # Crea la directory di output se non esiste
    os.makedirs(output_dir, exist_ok=True)

    # Salva il grafico come immagine
    output_path = os.path.join(output_dir, 'grafico_latency_throughput_'+DATE+'.png')
    plt.savefig(output_path, bbox_inches='tight')
    print(f'Grafico salvato come {output_path}')

    # Chiudi la figura per liberare memoria
    plt.close()

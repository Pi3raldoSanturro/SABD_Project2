import pandas as pd
import matplotlib.pyplot as plt
import os

# Leggi i dati dal file CSV
file_path = '../Results/Query3/output_statistics_2sec.csv'
data = pd.read_csv(file_path)

# Directory di output
output_dir = '../Graphics/Query3/latency_throughput/'

# Crea la directory di output se non esiste
os.makedirs(output_dir, exist_ok=True)

# Raggruppa i dati per vault_id
vault_ids = data['vault_id'].unique()

# Crea un grafico per ciascun vault_id e salva come immagine
for vault_id in vault_ids:
    vault_data = data[data['vault_id'] == vault_id]
    
    # Estrarre i valori di interesse
    latency = vault_data['latency']
    throughput = vault_data['throughput']
    
    # Plot
    plt.figure(figsize=(20, 6))
    plt.plot(latency, marker='.', linestyle='-', color='blue', label='latency')
    plt.plot(throughput, marker='.', linestyle='--', color='green', label='throughput')
    
    # Aggiungi titolo e etichette agli assi
    plt.title(f'Grafico dei valori di latency e throughput per Vault ID {vault_id}')
    plt.xlabel('Indice')
    plt.ylabel('Valori')
    
    # Aggiungi una griglia
    plt.grid(True)
    
    # Aggiungi una legenda
    plt.legend()
    
    # Salva il grafico come immagine
    output_path = os.path.join(output_dir, f'grafico_vault_id_{vault_id}.png')
    plt.savefig(output_path, bbox_inches='tight')
    print(f'Grafico salvato come {output_path}')
    
    # Chiudi la figura per liberare memoria
    plt.close()

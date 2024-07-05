import pandas as pd
import matplotlib.pyplot as plt
import os

# Leggi i dati dal file CSV
file_path = '../Results/Query3/output_statistics_2sec.csv'
data = pd.read_csv(file_path)

# Directory di output
output_dir = '../Graphics/Query3/'

# Crea la directory di output se non esiste
os.makedirs(output_dir, exist_ok=True)

# Raggruppa i dati per vault_id
vault_ids = data['vault_id'].unique()

# Crea un grafico per ciascun vault_id e salva come immagine
for vault_id in vault_ids:
    vault_data = data[data['vault_id'] == vault_id]
    
    # Estrarre i valori di interesse
    min_val = vault_data['min']
    percentile25 = vault_data['percentile25']
    percentile50 = vault_data['percentile50']
    percentile75 = vault_data['percentile75']
    max_val = vault_data['max']
    
    # Plot
    plt.figure(figsize=(20, 6))
    plt.plot(min_val, marker='o', linestyle='-', color='blue', label='min')
    plt.plot(percentile25, marker='o', linestyle='--', color='green', label='percentile25')
    plt.plot(percentile50, marker='o', linestyle='-', color='red', label='percentile50')
    plt.plot(percentile75, marker='o', linestyle='--', color='purple', label='percentile75')
    plt.plot(max_val, marker='o', linestyle='-', color='orange', label='max')
    
    # Aggiungi titolo e etichette agli assi
    plt.title(f'Grafico dei percentili per Vault ID {vault_id}')
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

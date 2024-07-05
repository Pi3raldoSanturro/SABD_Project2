import pandas as pd
import matplotlib.pyplot as plt
import os

# Leggi i dati dal file CSV
file_path = '../Results/Query1/Query1_all_days/output_all_days.csv'
data = pd.read_csv(file_path)

# Directory di output
output_dir = '../Graphics/Query1/'

# Crea la directory di output se non esiste
os.makedirs(output_dir, exist_ok=True)

# Raggruppa i dati per vault_id
vault_ids = data['vault_id'].unique()

# Crea un grafico per ciascun vault_id e salva come immagine
for vault_id in vault_ids:
    vault_data = data[data['vault_id'] == vault_id]
    
    # Estrarre i valori di mean_s194, stddev_s194 e window
    mean_s194 = vault_data['mean_s194']
    stddev_s194 = vault_data['stddev_s194']
    
    # Plot
    plt.figure(figsize=(20, 6))
    plt.plot(mean_s194, marker='o', linestyle='-', label='mean_s194', color= 'blue')
    plt.plot(stddev_s194, marker='o', linestyle='-', label='stddev_s194',color='red')
    
    # Aggiungi titolo e etichette agli assi
    plt.title(f'Grafico dei valori di mean_s194 e stddev_s194 per Vault ID {vault_id}')
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

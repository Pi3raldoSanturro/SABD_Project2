#!/bin/bash



# Array con i nomi delle query
queries=("Query1_1_day" "Query1_3_days" "Query1_all_days" "Query2_1_day" "Query2_3_days" "Query2_all_days" "Query3")

# Ciclo attraverso ogni query
for query in "${queries[@]}"; do
  # Path della cartella target
  target_dir="./Flink_Queries/$query/"
  
  # Verifica se la cartella target esiste
  if [ -d "$target_dir" ]; then
    # Naviga nella cartella target
    cd "$target_dir"
    
    # Esegui mvn clean package
    mvn clean package
    
    # Torna alla cartella principale
    cd - > /dev/null
  else
    echo "La cartella $target_dir non esiste"
  fi
done

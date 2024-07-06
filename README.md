# SABD_Project2
Second project for System and Architecture for Big Data course in University of Rome Tor Vergata


Per il corretto funzionamento del progetto, è necessario creare le seguente struttura di directory vuote nel file principale del progetto:
1. Una directory 'dataset' in cui va inserito il dataset batch chiamato 'dataset.csv'
2. Una directory 'Flink' e varie sotto-cartelle che avranno questa struttura:

Flink/
  ├── jobs/
  └── Results/
        ├── Query1/
        │ ├── Query1_1_day/
        │ ├── Query1_3_days/
        │ └── Query1_all_days/
        ├── Query2/
        │ ├── Query2_1_day/
        │ ├── Query2_3_days/
        │ └── Query2_all_days/
        └── Query3/

Tali cartelle saranno molto importanti per la sottomissione dei file .jar generati a Flink e la visualizzazione dei risultati in tempo reale.

Per l'esecuzione del progetto è necessario installare delle librerie python:

pip install panda
pip install pandas kafka-python-ng
pip install pandas pyarrow

Se ci sono problemi con l'installazione, è possibile utilizzare il flag '--break-system-packages'. Alternativamente, è possibile inizializzare un ambiente virtuale python all'interno della directory principale del progetto:

python3 -m venv venv
source venv/bin/activate

pip install panda
pip install pandas kafka-python-ng
pip install pandas pyarrow

Una volta terminato il progetto, utilizzare il comando seguente per terminare l'ambiente virtuale:

deactivate





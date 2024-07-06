# SABD_Project2
Second project for System and Architecture for Big Data course in University of Rome Tor Vergata

## Struttura delle Directory

Per il corretto funzionamento del progetto, è necessario creare la seguente struttura di directory vuote nel file principale del progetto:

1. Una directory `dataset` in cui va inserito il dataset batch chiamato `dataset.csv`.
2. Una directory `Flink` e varie sotto-cartelle che avranno questa struttura:
    - Una cartella principale `Flink` con due sottocartelle `jobs` e `Results`
    - All'interno di `Results` avremo tre sottocartelle: `Query1`, `Query2` e `Query3`
    - All'interno di `Query1` avremo tre sottocartelle: `Query1_1_day`, `Query1_3_days`, `Query1_all_days`
    - All'interno di `Query2` avremo tre sottocartelle: `Query2_1_day`, `Query2_3_days`, `Query2_all_days`

Visualizzare in raw questa struttura per una migliore comprensione:

Flink/<br>
  ├── jobs/<br>
  └── Results/<br>
              ├── Query1/<br>
              │   ├── Query1_1_day/<br>
              │   ├── Query1_3_days/<br>
              │   └── Query1_all_days/<br>
              ├── Query2/<br>
              │   ├── Query2_1_day/<br>
              │   ├── Query2_3_days/<br>
              │   └── Query2_all_days/<br>
              └── Query3/<br>



Tali cartelle saranno molto importanti per la sottomissione dei file .jar generati a Flink e la visualizzazione dei risultati in tempo reale.

## Installazione delle Librerie Python

Per l'esecuzione del progetto è necessario installare delle librerie python:

```bash
pip install panda
pip install pandas kafka-python-ng
pip install pandas pyarrow
```

Se ci sono problemi con l'installazione, è possibile utilizzare il flag '--break-system-packages'. Alternativamente, è possibile inizializzare un ambiente virtuale python all'interno della directory principale del progetto:

```bash
python3 -m venv venv
source venv/bin/activate

pip install panda
pip install pandas kafka-python-ng
pip install pandas pyarrow

```
Una volta terminato il progetto, utilizzare il comando seguente per terminare l'ambiente virtuale:
```bash
deactivate
```

## Start del progetto
Aprire un terminale nella directory principale del progetto ed eseguire il comando
```bash
./start_project.sh
```
Durante l'esecuzione verranno aperti altri terminali.
Tempo di esecuzione totale del progetto: circa 20 minuti

## Altre Directories

1. Nella cartella `Results` sono presenti i risultati ottenuti per un'intera run del progetto.
2. La cartella `Kafka` contiene alcuni scripts relativi all'esecuzione Kafka.
3. La cartella `Flink_Queries` contiene il codice relativo alle Queries
4. La cartella `scripts` contiene vari script di esecuzione e non
5. Nel file Graphics.zip sono presenti alcuni grafici relativi ai risultati e alle misurazioni di Throughput e Latenza misurati. Gli script di esecuzione per l'ottenimento di questi grafici si trova nella cartella `scripts`, e per la sua esecuzione sarà necessaria l'installazione di un'ulteriore libreria Python:
```bash
python3 -m pip install -U matplotlib
```




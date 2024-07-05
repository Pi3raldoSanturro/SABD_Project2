import pandas as pd

# Carica il dataset
df = pd.read_csv('../dataset/filtered_dataset.csv')

# Filtra le righe con data '2023-04-23' e failure pari a 1
filtered_df = df[(df['date'] == '2023-04-23') & (df['failure'] == 1)]

# Stampa le righe filtrate
if not filtered_df.empty:
    print("Rows with failure=1 on 2023-04-23:")
    print(filtered_df)
else:
    print("No rows with failure=1 on 2023-04-23")

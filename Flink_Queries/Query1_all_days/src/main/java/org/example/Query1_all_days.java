package org.example;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.util.Collector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Query1_all_days {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "kafka-broker:9092");
        properties.setProperty("group.id", "flink-group");

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                "batch-dataset",
                new SimpleStringSchema(),
                properties
        );

        consumer.setStartFromEarliest();

        DataStream<String> stream = env.addSource(consumer)
                .name("Kafka Source")
                .setParallelism(1)
                .map(value -> value)
                .filter(value -> {
                    // Parse JSON and filter by vault_id and s194_temperature_celsius
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(value);
                    int vaultId = node.get("vault_id").asInt();
                    double s194TemperatureCelsius = node.get("s194_temperature_celsius").asDouble();
                    return vaultId >= 1000 && vaultId <= 1020 && s194TemperatureCelsius != 0;
                })
                .name("Filter by Vault ID and s194_temperature_celsius")
                .setParallelism(1);

        // Apply a tumbling window of 5 seconds
        stream
                .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .apply(new CalculateStatistics())
                .setParallelism(1);

        env.execute("Query1_all_days");
    }

    // ProcessWindowFunction to calculate statistics and monitor latency and throughput
    public static class CalculateStatistics implements AllWindowFunction<String, String, TimeWindow> {
        @Override
        public void apply(TimeWindow window, Iterable<String> values, Collector<String> out) throws Exception {
            Map<Integer, List<Double>> vaultData = new HashMap<>();
            long windowStart = window.getStart();
            long windowEnd = window.getEnd();
            long currentTime = System.currentTimeMillis();
            long count = 0;

            for (String value : values) {
                count++;
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(value);
                int vaultId = node.get("vault_id").asInt();
                double s194TemperatureCelsius = node.get("s194_temperature_celsius").asDouble();

                vaultData.computeIfAbsent(vaultId, k -> new ArrayList<>()).add(s194TemperatureCelsius);
            }

            long latency = currentTime - windowStart; // Calculate latency
            double throughput = count / 5.0; // Calculate throughput for a 5-second window

            StringBuilder csvBatch = new StringBuilder();
            for (Map.Entry<Integer, List<Double>> entry : vaultData.entrySet()) {
                int vaultId = entry.getKey();
                List<Double> temperatures = entry.getValue();
                int recordCount = temperatures.size();
                double mean = temperatures.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double variance = temperatures.stream().mapToDouble(t -> Math.pow(t - mean, 2)).average().orElse(0.0);
                double stdDev = Math.sqrt(variance);

                // Prepare the output line
                csvBatch.append(windowStart).append(" - ").append(windowEnd).append(",")
                        .append(vaultId).append(",")
                        .append(recordCount).append(",")
                        .append(mean).append(",")
                        .append(stdDev).append(",")
                        .append(latency).append(",")
                        .append(throughput).append("\n");
            }

            // Write the batch to a file
            File file = new File("/opt/flink/Results/Query1/Query1_all_days/output_all_days.csv");
            boolean fileExists = file.exists() && file.length() != 0;
            try (FileWriter writer = new FileWriter(file, true)) {
                if (!fileExists) {
                    writer.write("window,vault_id,count,mean_s194,stddev_s194,latency,throughput\n");
                }
                writer.write(csvBatch.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Output the batch as a single string
            out.collect(csvBatch.toString());
        }
    }
}

/*
Il throughput è una misura della quantità di dati processati dal sistema in un determinato periodo di tempo.
Nel contesto di questo codice, il throughput è calcolato per ogni finestra temporale di 5 secondi.

Come viene calcolato il throughput:
1. Durante l'elaborazione dei dati nella finestra, viene mantenuto un conteggio (`count`) del numero di record processati.
2. Al termine della finestra, il throughput viene calcolato dividendo il numero di record processati (`count`) per la durata della finestra in secondi.
   - In questo caso, la durata della finestra è 5 secondi.
   - Quindi, il throughput = count / 5.0
3. Questo valore di throughput rappresenta il numero medio di record processati al secondo durante la finestra temporale di 5 secondi.
4. Il throughput viene poi incluso nel file di output insieme alle altre statistiche.

Questa semplice formula consente di monitorare l'efficienza del sistema in termini di capacità di elaborazione dei dati in tempo reale.
*/

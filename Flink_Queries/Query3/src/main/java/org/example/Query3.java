package org.example;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import com.tdunning.math.stats.MergingDigest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Query3 {
    // Define the global variable SECONDS
    public static int SECONDS = 2; // Default value, can be changed before execution

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
                    // Parse JSON and filter by vault_id
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(value);
                    int vaultId = node.get("vault_id").asInt();
                    return vaultId >= 1090 && vaultId <= 1120;
                })
                .name("Filter by Vault ID")
                .setParallelism(1);

        // Apply a tumbling window of SECONDS second
        stream
                .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(SECONDS)))
                .apply(new CalculateStatistics())
                .setParallelism(1);

        // Execute the Flink job
        env.execute("Query3_" + SECONDS);
    }

    // ProcessWindowFunction to calculate statistics
    public static class CalculateStatistics implements AllWindowFunction<String, String, TimeWindow> {
        @Override
        public void apply(TimeWindow window, Iterable<String> values, Collector<String> out) throws Exception {
            Map<String, Double> lastValues = new HashMap<>();
            Map<Integer, List<Double>> vaultData = new HashMap<>();
            long count = 0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;

            long windowStart = window.getStart();
            long currentTime = System.currentTimeMillis();

            for (String value : values) {
                count++;
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(value);
                String serialNumber = node.get("serial_number").asText();
                double powerOnHours = node.get("s9_power_on_hours").asDouble();
                int vaultId = node.get("vault_id").asInt();

                if (!lastValues.containsKey(serialNumber) || lastValues.get(serialNumber) < powerOnHours) {
                    lastValues.put(serialNumber, powerOnHours);
                }

                vaultData.computeIfAbsent(vaultId, k -> new ArrayList<>()).add(powerOnHours);
            }

            long latency = currentTime - windowStart; // Calculate latency
            double throughput = count / (double) SECONDS; // Calculate throughput for the window

            StringBuilder csvBatch = new StringBuilder();

            for (Map.Entry<Integer, List<Double>> entry : vaultData.entrySet()) {
                int vaultId = entry.getKey();
                List<Double> powerOnHoursList = entry.getValue();
                MergingDigest tDigest = new MergingDigest(100); // Use MergingDigest instead of TDigest
                powerOnHoursList.forEach(tDigest::add);

                min = Collections.min(powerOnHoursList);
                max = Collections.max(powerOnHoursList);
                long vaultCount = powerOnHoursList.size();
                double percentile25 = tDigest.quantile(0.25);
                double percentile50 = tDigest.quantile(0.50);
                double percentile75 = tDigest.quantile(0.75);

                String result = String.format("%d,%d,%f,%f,%f,%f,%f,%d,%d,%f",
                        windowStart,
                        vaultId,
                        min,
                        percentile25,
                        percentile50,
                        percentile75,
                        max,
                        vaultCount,
                        latency,
                        throughput);

                csvBatch.append(result).append("\n");
                out.collect(result);
            }

            // Write to file
            File file = new File("/opt/flink/Results/Query3/output_statistics_" + SECONDS + "sec.csv");
            boolean fileExists = file.exists() && file.length() != 0;
            try (FileWriter writer = new FileWriter(file, true)) {
                if (!fileExists) {
                    writer.write("window,vault_id,min,percentile25,percentile50,percentile75,max,count,latency,throughput\n");
                }
                writer.write(csvBatch.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/*
Il throughput è una misura della quantità di dati processati dal sistema in un determinato periodo di tempo.
Nel contesto di questo codice, il throughput è calcolato per ogni finestra temporale di lunghezza variabile definita da SECONDS.

Come viene calcolato il throughput:
1. Durante l'elaborazione dei dati nella finestra, viene mantenuto un conteggio (`count`) del numero di record processati.
2. Al termine della finestra, il throughput viene calcolato dividendo il numero di record processati (`count`) per la durata della finestra in secondi.
   - In questo caso, la durata della finestra è definita dalla variabile SECONDS.
   - Quindi, il throughput = count / SECONDS
3. Questo valore di throughput rappresenta il numero medio di record processati al secondo durante la finestra temporale.
4. Il throughput viene poi incluso nel file di output insieme alle altre statistiche.

Questa semplice formula consente di monitorare l'efficienza del sistema in termini di capacità di elaborazione dei dati in tempo reale.
*/

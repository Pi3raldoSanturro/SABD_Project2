package org.example;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.util.Collector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Query2_3_days {
    private static final Map<String, Map<Integer, VaultInfo>> globalVaultData = new HashMap<>();

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
                    // Parse JSON and filter by failure and date
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(value);
                    int failure = node.get("failure").asInt();
                    String date = node.get("date").asText();
                    return failure == 1 && ("2023-04-01".equals(date) || "2023-04-02".equals(date) || "2023-04-03".equals(date));
                })
                .name("Filter by Failure and Date")
                .setParallelism(1);

        // Apply a tumbling window of 5 seconds
        stream
                .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .apply(new CalculateStatistics())
                .setParallelism(1);

        env.execute("Query2_3_days");
    }

    // ProcessWindowFunction to calculate statistics and monitor latency and throughput
    public static class CalculateStatistics implements AllWindowFunction<String, String, TimeWindow> {
        @Override
        public void apply(TimeWindow window, Iterable<String> values, Collector<String> out) throws Exception {
            long windowStart = window.getStart();
            long currentTime = System.currentTimeMillis();
            long count = 0;

            // Update global vault data with new values from the current window
            for (String value : values) {
                count++;
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(value);
                int vaultId = node.get("vault_id").asInt();
                String model = node.get("model").asText();
                String serialNumber = node.get("serial_number").asText();
                String date = node.get("date").asText();

                Map<Integer, VaultInfo> dateVaultData = globalVaultData.getOrDefault(date, new HashMap<>());
                VaultInfo vaultInfo = dateVaultData.getOrDefault(vaultId, new VaultInfo());
                vaultInfo.incrementFailures();
                vaultInfo.addModelSerial(model, serialNumber);

                dateVaultData.put(vaultId, vaultInfo);
                globalVaultData.put(date, dateVaultData);
            }

            long latency = currentTime - windowStart; // Calculate latency
            double throughput = count / 5.0; // Calculate throughput for a 5-second window

            // Prepare and write output for each date
            for (String date : globalVaultData.keySet()) {
                Map<Integer, VaultInfo> dateVaultData = globalVaultData.get(date);

                // Get top 10 vaults by number of failures for the date
                List<Map.Entry<Integer, VaultInfo>> topVaults = dateVaultData.entrySet().stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue().getFailures(), e1.getValue().getFailures()))
                        .limit(10)
                        .collect(Collectors.toList());

                // Prepare the output line
                StringBuilder csvBatch = new StringBuilder();
                csvBatch.append(windowStart).append(",")
                        .append(latency).append(",")
                        .append(throughput).append(",");

                for (int i = 0; i < 10; i++) {
                    if (i < topVaults.size()) {
                        Map.Entry<Integer, VaultInfo> entry = topVaults.get(i);
                        int vaultId = entry.getKey();
                        VaultInfo vaultInfo = entry.getValue();
                        csvBatch.append(vaultId).append(",")
                                .append(vaultInfo.getFailures()).append(",")
                                .append(vaultInfo.getModelSerialList()).append(",");
                    } else {
                        // Fill with empty values if there are less than 10 vaults
                        csvBatch.append(" , , , ,");
                    }
                }

                // Remove the last comma and add a newline
                if (csvBatch.length() > 0) {
                    csvBatch.setLength(csvBatch.length() - 1);
                }
                csvBatch.append("\n");

                // Write the batch to a file
                File file = new File("/opt/flink/Results/Query2/Query2_3_days/output_" + date + ".csv");
                boolean fileExists = file.exists() && file.length() != 0;
                try (FileWriter writer = new FileWriter(file, true)) {
                    if (!fileExists) {
                        writer.write("window,latency,throughput,vault_id1,failures1,model_serial_list1,...,vault_id10,failures10,model_serial_list10\n");
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

    // Helper class to store failure count and model/serial numbers
    private static class VaultInfo {
        private int failures;
        private final List<String> modelSerialList;

        public VaultInfo() {
            this.failures = 0;
            this.modelSerialList = new ArrayList<>();
        }

        public void incrementFailures() {
            this.failures++;
        }

        public int getFailures() {
            return failures;
        }

        public void addModelSerial(String model, String serial) {
            this.modelSerialList.add("[" + model + ", " + serial + "]");
        }

        public String getModelSerialList() {
            return String.join(", ", modelSerialList);
        }
    }
}



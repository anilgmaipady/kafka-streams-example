package com.clients.producer;

import com.model.StockTransaction;
import com.util.datagen.DataGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class will produce 100 Purchase records per iteration
 * for a total of 1,000 records in one minute then it will shutdown.
 */
public class MockDataProducer {

    private static final Logger LOG = LoggerFactory.getLogger(MockDataProducer.class);

    private static Producer<String, String> producer;
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static Callback callback;
    public static final String STOCK_TRANSACTIONS_TOPIC = "stock-transactions";
    private static volatile boolean keepRunning = true;

    /**
     * This method runs on the main thread and is expected to run as part of a
     * standalone program and not embedded as it will block until completion
     */
    public static void produceStockTransactionsForIQ() {
            init();
            while (keepRunning) {
                List<StockTransaction> transactions = DataGenerator.generateStockTransactionsForIQ(100);
                for (StockTransaction transaction : transactions) {
                    String jsonTransaction = convertToJson(transaction);
                    ProducerRecord<String, String> record = new ProducerRecord<>(STOCK_TRANSACTIONS_TOPIC, transaction.getSymbol(), jsonTransaction);
                    producer.send(record, callback);
                }
                LOG.info("Stock Transactions for IQ Sent");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }

              LOG.info("Done generating transactions for IQ");
            }
    }


    public static void shutdown() {
        LOG.info("Shutting down data generation");
        keepRunning = false;

        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
        if (producer != null) {
            producer.close();
            producer = null;
        }

    }

    private static void init() {
        if (producer == null) {
            LOG.info("Initializing the producer");
            Properties properties = new Properties();
            properties.put("bootstrap.servers", "localhost:9092");
            properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("acks", "1");
            properties.put("retries", "3");

            producer = new KafkaProducer<>(properties);

            callback = (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                }
            };
            LOG.info("Producer initialized");
        }
    }

    //KEEP
    private static <T> String convertToJson(T generatedDataItem) {
        return gson.toJson(generatedDataItem);
    }
}

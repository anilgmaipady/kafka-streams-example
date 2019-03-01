package com.exmples;


import com.clients.producer.MockDataProducer;

public class StockPerformanceInteractiveQueryDataProducer {

    public static void main(String[] args) {
        MockDataProducer.produceStockTransactionsForIQ();
        Runtime.getRuntime().addShutdownHook(new Thread(MockDataProducer::shutdown));
    }
}

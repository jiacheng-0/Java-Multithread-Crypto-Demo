package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BinanceRunnable implements Runnable {
    private final String url;

    public BinanceRunnable(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        long startTime = System.nanoTime();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(responseBody -> {
                    long endTime = System.nanoTime();
                    long duration = endTime - startTime;
                    double durationInSeconds = duration / 1_000_000_000.0;
                    System.out.printf("Request-Response Time: %.2f seconds%n", durationInSeconds);
                    parse(responseBody);
                })
                .join();
    }

    public static void parse(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(responseBody);
            String symbol = node.get("symbol").asText();
            String bidPrice = node.get("bidPrice").asText();
            String bidQty = node.get("bidQty").asText();
            String askPrice = node.get("askPrice").asText();
            String askQty = node.get("askQty").asText();

            System.out.println("Symbol: " + symbol);
            System.out.println("Bid Price: " + bidPrice);
            System.out.println("Bid Quantity: " + bidQty);
            System.out.println("Ask Price: " + askPrice);
            System.out.println("Ask Quantity: " + askQty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String url = "https://api.binance.com/api/v3/ticker/bookTicker?symbol=BTCUSDT";
        BinanceRunnable example = new BinanceRunnable(url);
        Thread thread = new Thread(example);
        thread.start();
    }
}

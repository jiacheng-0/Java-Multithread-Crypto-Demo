package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BinanceRunnable implements Runnable {
    private final String url;

    private TickerInfo tickerInfo;
    private String tickerSymbol;

    public BinanceRunnable(String url, String tickerSymbol) {
        this.url = url;
        this.tickerSymbol = tickerSymbol;
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

    private void parse(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(responseBody);
            String symbol = node.get("symbol").asText();
            double bidPrice = node.get("bidPrice").asDouble();
            double bidQty = node.get("bidQty").asDouble();
            double askPrice = node.get("askPrice").asDouble();
            double askQty = node.get("askQty").asDouble();

            tickerInfo = new TickerInfo(symbol, bidPrice, bidQty, askPrice, askQty);
            System.out.println(tickerInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TickerInfo getTickerInfo() {
        return tickerInfo;
    }

    public static void main(String[] args) {
        String url = "https://api.binance.com/api/v3/ticker/bookTicker?symbol=BTCUSDT";
        BinanceRunnable example = new BinanceRunnable(url);
        Thread thread = new Thread(example);
        thread.start();
    }
}

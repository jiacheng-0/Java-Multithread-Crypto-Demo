package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HuobiRunnable implements Runnable {
    private final String url;
    private TickerInfo tickerInfo;
    private String tickerSymbol;

    public HuobiRunnable(String url, String tickerSymbol) {
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
            JsonNode dataArray = node.get("data");

            for (JsonNode data : dataArray) {
                String symbol = data.get("symbol").asText();
                if ("btcusdt".equals(symbol) || "ethusdt".equals(symbol)) {
                    double bid = data.get("bid").asDouble();
                    double bidSize = data.get("bidSize").asDouble();
                    double ask = data.get("ask").asDouble();
                    double askSize = data.get("askSize").asDouble();

                    tickerInfo = new TickerInfo(symbol, bid, bidSize, ask, askSize);
                    System.out.println(tickerInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TickerInfo getTickerInfo() {
        return tickerInfo;
    }

    public static void main(String[] args) {
        String url = "https://api.huobi.pro/market/tickers";
        HuobiRunnable example = new HuobiRunnable(url);
        Thread thread = new Thread(example);
        thread.start();
    }
}

package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BinanceApiExample {
    public static void main(String[] args) {
        String url = "https://api.binance.com/api/v3/ticker/bookTicker?symbol=BTCUSDT";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(BinanceApiExample::parse)
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
}

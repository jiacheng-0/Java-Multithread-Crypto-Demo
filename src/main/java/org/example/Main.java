package org.example;

public class Main {
    public static void main(String[] args) {

        String tickerSymbol = "BTCUSDT";
        String binanceUrl = "https://api.binance.com/api/v3/ticker/bookTicker?symbol=";
        String huobiUrl = "https://api.huobi.pro/market/tickers";

        BinanceRunnable binanceRunnable = new BinanceRunnable(binanceUrl, tickerSymbol);
        HuobiRunnable huobiRunnable = new HuobiRunnable(huobiUrl, tickerSymbol);

        Thread binanceThread = new Thread(binanceRunnable);
        Thread huobiThread = new Thread(huobiRunnable);

        binanceThread.start();
        huobiThread.start();

        try {
            binanceThread.join();
            huobiThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TickerInfo binanceInfo = binanceRunnable.getTickerInfo();
        TickerInfo huobiInfo = huobiRunnable.getTickerInfo();

        if (binanceInfo != null && huobiInfo != null) {
            double bestBidPrice = Math.max(binanceInfo.getBidPrice(), huobiInfo.getBidPrice());
            double bestAskPrice = Math.min(binanceInfo.getAskPrice(), huobiInfo.getAskPrice());

            System.out.println("Best Bid Price: " + bestBidPrice);
            System.out.println("Best Ask Price: " + bestAskPrice);
        } else {
            System.out.println("Failed to retrieve ticker information from one or both sources.");
        }
    }
}

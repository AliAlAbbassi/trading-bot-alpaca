package hello;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import net.jacobpeterson.abstracts.websocket.exception.WebsocketException;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.enums.marketdata.BarsTimeFrame;
import net.jacobpeterson.alpaca.rest.exception.AlpacaAPIRequestException;
import net.jacobpeterson.alpaca.websocket.broker.listener.AlpacaStreamListener;
import net.jacobpeterson.alpaca.websocket.broker.listener.AlpacaStreamListenerAdapter;
import net.jacobpeterson.alpaca.websocket.broker.message.AlpacaStreamMessageType;
import net.jacobpeterson.alpaca.websocket.marketdata.listener.MarketDataListener;
import net.jacobpeterson.alpaca.websocket.marketdata.listener.MarketDataListenerAdapter;
import net.jacobpeterson.alpaca.websocket.marketdata.message.MarketDataMessageType;
import net.jacobpeterson.domain.alpaca.marketdata.historical.bar.BarsResponse;
import net.jacobpeterson.domain.alpaca.marketdata.historical.quote.QuotesResponse;
import net.jacobpeterson.domain.alpaca.marketdata.historical.trade.TradesResponse;
import net.jacobpeterson.domain.alpaca.marketdata.realtime.MarketDataMessage;
import net.jacobpeterson.domain.alpaca.marketdata.realtime.bar.BarMessage;
import net.jacobpeterson.domain.alpaca.marketdata.realtime.quote.QuoteMessage;
import net.jacobpeterson.domain.alpaca.marketdata.realtime.trade.TradeMessage;
import net.jacobpeterson.domain.alpaca.streaming.AlpacaStreamMessage;
import net.jacobpeterson.domain.alpaca.streaming.account.AccountUpdateMessage;
import net.jacobpeterson.domain.alpaca.streaming.trade.TradeUpdateMessage;

public class WsbAlgorithm {

    AlpacaAPI alpacaAPI;
    private state wsbState;

    public WsbAlgorithm(AlpacaAPI alpacaAPI) {
        this.alpacaAPI = alpacaAPI;
    }

    public void livePerformance() {
        try {
            // List to account updates and trade updates from Alpaca and print their
            // messages out
            AlpacaStreamListener alpacaStreamListener = new AlpacaStreamListenerAdapter(
                    AlpacaStreamMessageType.ACCOUNT_UPDATES, AlpacaStreamMessageType.TRADE_UPDATES) {
                @Override
                public void onStreamUpdate(AlpacaStreamMessageType streamMessageType,
                        AlpacaStreamMessage streamMessage) {
                    switch (streamMessageType) {
                        case ACCOUNT_UPDATES:
                            System.out.println((AccountUpdateMessage) streamMessage);
                            break;
                        case TRADE_UPDATES:
                            System.out.println((TradeUpdateMessage) streamMessage);
                            break;
                        default:
                            break;
                    }
                }
            };

            // Add the 'AlpacaStreamListener'
            // Note that when the first 'AlpacaStreamListener' is added, the Websocket
            // connection is created.
            alpacaAPI.addAlpacaStreamListener(alpacaStreamListener);

            // Wait for 5 seconds
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Remove the 'AlpacaStreamListener'
            // Note that when the last 'AlpacaStreamListener' is removed, the Websocket
            // connection is closed.
            alpacaAPI.removeAlpacaStreamListener(alpacaStreamListener);
        } catch (WebsocketException exception) {
            exception.printStackTrace();
        }
    }

    public void getFirstHundredTrades() {
        try {
            // Get first 100 trades of a stock of first minute on 3/1/2021 and print them
            // out
            TradesResponse appleTradesResponse = alpacaAPI.getTrades(this.wsbState.getSymbol(),
                    ZonedDateTime.of(2021, 3, 1, 9, 30, 0, 0, ZoneId.of("America/New_York")),
                    ZonedDateTime.of(2021, 3, 1, 9, 31, 0, 0, ZoneId.of("America/New_York")), 100, null);
            appleTradesResponse.getTrades().forEach(System.out::println);
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }
    }

    public void getFirstHundredQuotes() {
        try {
            // Get first 100 quotes of AAPL of first minute on 3/1/2021 and print them out
            if (this.wsbState.isDateNull()) {
                QuotesResponse appleQuotesResponse = alpacaAPI.getQuotes(this.wsbState.getSymbol(),
                        ZonedDateTime.of(2021, 3, 1, 9, 30, 0, 0, ZoneId.of("America/New_York")),
                        ZonedDateTime.of(2021, 3, 1, 9, 31, 0, 0, ZoneId.of("America/New_York")), 100, null);
                appleQuotesResponse.getQuotes().forEach(System.out::println);
            } else {
                QuotesResponse appleQuotesResponse = alpacaAPI.getQuotes(this.wsbState.getSymbol(),
                        this.wsbState.getStartTime(), this.wsbState.getEndTime(), 100, null);
                appleQuotesResponse.getQuotes().forEach(System.out::println);
            }
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }
    }

    public void getBars() {
        try {
            // Get hour bars of AAPL from 2/22/2021 at 9:30 AM to 2/24/2021 at 4 PM and
            // print them out
            BarsResponse appleBarsResponse = alpacaAPI.getBars(this.wsbState.getSymbol(),
                    ZonedDateTime.of(2021, 2, 22, 9, 30, 0, 0, ZoneId.of("America/New_York")),
                    ZonedDateTime.of(2021, 2, 24, 12 + 4, 0, 0, 0, ZoneId.of("America/New_York")), null, null,
                    BarsTimeFrame.HOUR);
            appleBarsResponse.getBars().forEach(System.out::println);
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }
    }

    public void realTime() {
        try {
            // Listen to stock quotes, trades, and minute bars and print their messages out
            MarketDataListener listenerTSLA = new MarketDataListenerAdapter(this.wsbState.getSymbol(),
                    MarketDataMessageType.TRADE, MarketDataMessageType.QUOTE, MarketDataMessageType.BAR) {
                @Override
                public void onStreamUpdate(MarketDataMessageType streamMessageType, MarketDataMessage streamMessage) {
                    switch (streamMessageType) {
                        case TRADE:
                            TradeMessage tradeMessage = (TradeMessage) streamMessage;
                            System.out.printf("Trade: Price=%.2f Size=%d Time=%s\n", tradeMessage.getPrice(),
                                    tradeMessage.getSize(), tradeMessage.getTimestamp());
                            break;
                        case QUOTE:
                            QuoteMessage quoteMessage = (QuoteMessage) streamMessage;
                            System.out.printf("Quote: Ask Price=%.2f Ask Size=%d Bid Price=%.2f Bid Size=%d Time=%s\n",
                                    quoteMessage.getAskPrice(), quoteMessage.getAskSize(), quoteMessage.getBidPrice(),
                                    quoteMessage.getBidSize(), quoteMessage.getTimestamp());
                            break;
                        case BAR:
                            BarMessage barMessage = (BarMessage) streamMessage;
                            System.out.printf("Bar: O=%.2f H=%.2f L=%.2f C=%.2f Time=%s \n", barMessage.getOpen(),
                                    barMessage.getHigh(), barMessage.getLow(), barMessage.getClose(),
                                    barMessage.getTimestamp());
                            break;
                    }
                }
            };

            // Add the 'MarketDataListener'
            // Note that when the first 'MarketDataListener' is added, the Websocket
            // connection is created.
            alpacaAPI.addMarketDataStreamListener(listenerTSLA);

            // Wait for 5 seconds
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Remove the 'MarketDataListener'
            // Note that when the last 'MarketDataListener' is removed, the Websocket
            // connection is closed.
            alpacaAPI.removeMarketDataStreamListener(listenerTSLA);
        } catch (WebsocketException exception) {
            exception.printStackTrace();
        }
    }

    public state getWsbState() {
        return this.wsbState;
    }

    public void setWsbState(state state) {
        this.wsbState = state;
    }
}

package com.demo.ticker.tickerapp.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.demo.ticker.tickerapp.entity.TransactionPair;
import com.demo.ticker.tickerapp.tasks.BackgroundCheckTask;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * @author bhusu01
 */
@Service
public class TickerUpdateService {

    private static final String TICKER_ENDPOINT = "https://api.kraken.com/0/public/Ticker?pair=";

    public String helloUniverse() {
        return "Hello Universe";
    }

    final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public String fetchTicker(String tick) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(TICKER_ENDPOINT + tick).asJson();
            JSONObject result = response.getBody().getObject().getJSONObject("result");
            String internalTick = result.keys().next();
            JSONObject tickQuote = result.getJSONObject(internalTick);
            return tickQuote.getJSONArray("c").getString(0);
        } catch (UnirestException e) {
            return e.getMessage();
        }
    }

    public float fetchCurrentProfit(TransactionPair pair) {
        float unitPrice = pair.fetchUnitPrice();
        String tick = pair.fetchTick();
        float currentPrice = Float.parseFloat(fetchTicker(tick));
        float diff = currentPrice - unitPrice;
        return pair.buyAmount() * diff;
    }

    public void submitTask(TransactionPair pair) {
        BackgroundCheckTask task = new BackgroundCheckTask(pair, this);
        executorService.submit(task);
    }
}






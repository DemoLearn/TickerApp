package com.demo.ticker.tickerapp.tasks;

import javax.validation.constraints.NotNull;

import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.ticker.tickerapp.entity.TransactionPair;
import com.demo.ticker.tickerapp.service.callback.ValueUpdate;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * @author bhusu01
 */
public class BackgroundCheckTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundCheckTask.class);
    private static final Long REFRESH_TIME_MILLIS = 5000L;
    private static final String TICKER_ENDPOINT = "https://api.kraken.com/0/public/Ticker?pair=";

    private final TransactionPair pair;
    private final UUID taskId;
    private final ValueUpdate updateListener;
    private boolean canRun = true;

    public BackgroundCheckTask(@NotNull TransactionPair pair,
                               @NotNull UUID taskId,
                               @NotNull ValueUpdate updateListener) {
        this.pair = pair;
        this.taskId = taskId;
        this.updateListener = updateListener;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Starting task");
            while (canRun) {
                float currentProfit = fetchCurrentProfit(pair);
                updateListener.updateLatest(taskId, currentProfit);
                LOGGER.info("Current profit is {}", currentProfit);
                if(Thread.currentThread().isInterrupted()) {
                    canRun = false;
                    break;
                }
                try {
                    Thread.sleep(REFRESH_TIME_MILLIS);
                } catch (InterruptedException e) {
                    LOGGER.info("Task interrupted. Exiting task.");
                    canRun = false;
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            LOGGER.error("There is an error ", e);
        }
    }

    public static String fetchTicker(String tick) {
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

    public static float fetchCurrentProfit(TransactionPair pair) {
        float unitPrice = pair.fetchUnitPrice();
        String tick = pair.fetchTick();
        float currentPrice = Float.parseFloat(fetchTicker(tick));
        float diff = currentPrice - unitPrice;
        return pair.buyAmount() * diff;
    }
}

package com.demo.ticker.tickerapp.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

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

}

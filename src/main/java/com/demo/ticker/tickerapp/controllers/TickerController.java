package com.demo.ticker.tickerapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.ticker.tickerapp.entity.TransactionPair;
import com.demo.ticker.tickerapp.service.TickerUpdateService;

/**
 * @author bhusu01
 */
@Controller
public class TickerController {

    @Autowired
    TickerUpdateService tickerUpdateService;

    @GetMapping(path = "/test")
    public ResponseEntity<String> testKrakenAPI(@RequestParam(name = "tick") String tick) {
        return new ResponseEntity<>(tickerUpdateService.fetchTicker(tick), HttpStatus.OK);
    }

    @PostMapping(path = "/transaction", consumes = "application/json")
    public ResponseEntity<Float> readTransaction(@RequestBody TransactionPair pair) {
        return new ResponseEntity<>(tickerUpdateService.fetchCurrentProfit(pair), HttpStatus.OK);

    }

    @PostMapping(path="/submitPair", consumes = "application/json")
    public ResponseEntity<String> submitPair(TransactionPair pair) {
        tickerUpdateService.submitTask(pair);
        return new ResponseEntity<>("Submitted", HttpStatus.OK);
    }

}

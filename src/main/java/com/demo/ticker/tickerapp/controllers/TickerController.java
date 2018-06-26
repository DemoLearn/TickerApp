package com.demo.ticker.tickerapp.controllers;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

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
import com.demo.ticker.tickerapp.tasks.BackgroundCheckTask;

/**
 * @author bhusu01
 */
@Controller
public class TickerController {

    @Autowired
    TickerUpdateService tickerUpdateService;

    @GetMapping(path = "/test")
    public ResponseEntity<String> testKrakenAPI(@RequestParam(name = "tick") String tick) {
        return new ResponseEntity<>(BackgroundCheckTask.fetchTicker(tick), HttpStatus.OK);
    }

    @PostMapping(path = "/transaction", consumes = "application/json")
    public ResponseEntity<Float> readTransaction(@RequestBody @NotNull TransactionPair pair) {
        return new ResponseEntity<>(BackgroundCheckTask.fetchCurrentProfit(pair), HttpStatus.OK);

    }

    @PostMapping(path="/submitPair", consumes = "application/json")
    public ResponseEntity<String> submitPair(@RequestBody @NotNull TransactionPair pair) {
        UUID taskId = tickerUpdateService.submitTask(pair);
        return new ResponseEntity<>(taskId.toString(), HttpStatus.OK);
    }

    @GetMapping(path = "/tasks")
    public ResponseEntity<Map<UUID, TransactionPair>> listTasks() {
        return new ResponseEntity<>(tickerUpdateService.listRunningTasks(), HttpStatus.OK);
    }

    @PostMapping(path = "/cancelTask", consumes = "text/plain")
    public ResponseEntity<Boolean> cancelTask(@RequestBody String taskId) {
        return new ResponseEntity<>(tickerUpdateService.stopTask(UUID.fromString(taskId)), HttpStatus.OK);
    }



}

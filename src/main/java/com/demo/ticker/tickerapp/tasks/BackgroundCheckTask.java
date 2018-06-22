package com.demo.ticker.tickerapp.tasks;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.ticker.tickerapp.entity.TransactionPair;
import com.demo.ticker.tickerapp.service.TickerUpdateService;

/**
 * @author bhusu01
 */
public class BackgroundCheckTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundCheckTask.class);
    private static final Long REFRESH_TIME_MILLIS = 5000L;

    private final TransactionPair pair;
    private final TickerUpdateService updateService;

    public BackgroundCheckTask(@NotNull TransactionPair pair, @NotNull TickerUpdateService updateService) {
        this.pair = pair;
        this.updateService = updateService;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Starting task");
            while (true) {
                float currentProfit = updateService.fetchCurrentProfit(pair);
                LOGGER.info("Current profit is {}", currentProfit);
                try {
                    Thread.sleep(REFRESH_TIME_MILLIS);
                } catch (InterruptedException e) {
                    LOGGER.error("task interrupted ", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("There is an error ", e);
        }
    }
}

package com.demo.ticker.tickerapp.service.callback;

import java.util.UUID;

/**
 * @author bhusu01
 */
public interface ValueUpdate {

    public void updateLatest(UUID taskId, float profitValue);

}

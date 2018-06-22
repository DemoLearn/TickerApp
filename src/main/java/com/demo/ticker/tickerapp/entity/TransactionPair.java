package com.demo.ticker.tickerapp.entity;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author bhusu01
 */
@NoArgsConstructor
@Getter
public class TransactionPair {

    @NotNull
    Record buy;
    @NotNull
    Record sell;

    public float fetchUnitPrice() {
        return sell.amount/buy.amount;
    }

    public String fetchTick() {
        String tick = buy.curr + sell.curr;
        return tick.toUpperCase();
    }

    public float buyAmount() {
        return buy.amount;
    }

}

@NoArgsConstructor
@Getter
@Setter
class Record {
    @NotNull
    String curr;
    float amount;
}




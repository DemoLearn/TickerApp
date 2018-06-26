package com.demo.ticker.tickerapp.entity;

import java.util.concurrent.Future;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class TaskWrapper {
    @JsonIgnore
    final Future taskHandle;
    final TransactionPair pair;
    volatile float currentProfit;
}

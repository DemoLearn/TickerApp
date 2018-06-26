package com.demo.ticker.tickerapp.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.demo.ticker.tickerapp.entity.TaskWrapper;
import com.demo.ticker.tickerapp.entity.TransactionPair;
import com.demo.ticker.tickerapp.service.callback.ValueUpdate;
import com.demo.ticker.tickerapp.tasks.BackgroundCheckTask;

/**
 * @author bhusu01
 */
@Service
public class TickerUpdateService implements ValueUpdate {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickerUpdateService.class);

    public String helloUniverse() {
        return "Hello Universe";
    }

    final ExecutorService executorService = Executors.newFixedThreadPool(10);
    final Map<UUID, TaskWrapper> taskMap = new ConcurrentHashMap<>();

    public UUID submitTask(TransactionPair pair) {
        UUID taskId = UUID.randomUUID();
        BackgroundCheckTask task = new BackgroundCheckTask(pair, taskId, this);
        Future<?> taskHandle = executorService.submit(task);
        TaskWrapper wrapper = new TaskWrapper(taskHandle, pair);
        taskMap.put(taskId, wrapper);
        return taskId;
    }

    public Map<UUID, TaskWrapper> listRunningTasks(){
        return new HashMap<>(taskMap);
    }

    public TaskWrapper fetchTask(UUID taskId) {
        return taskMap.get(taskId);
    }

    public boolean stopTask(UUID taskId) {
        TaskWrapper taskWrapper = taskMap.remove(taskId);
        if(taskWrapper == null) {
            throw new IllegalArgumentException("No task with Id " + taskId);
        }
        Future taskHandle = taskWrapper.getTaskHandle();
        if(!taskHandle.isDone()) {
            LOGGER.info("Cancelling task {}", taskId);
            boolean status = taskHandle.cancel(true);
            LOGGER.info("Task {} cancelled: {}", taskId, status);
            return status;
        }
        throw new IllegalStateException("The task has already been stopped");
    }

    @Override
    public void updateLatest(UUID taskId, float profitValue) {
        TaskWrapper wrapper = taskMap.get(taskId);
        if(wrapper == null) {
            throw new IllegalStateException("Wrapper cannot be null. Task Id" + taskId.toString());
        }
        wrapper.setCurrentProfit(profitValue);
    }
}





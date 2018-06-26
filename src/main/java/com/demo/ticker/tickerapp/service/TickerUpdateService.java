package com.demo.ticker.tickerapp.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.demo.ticker.tickerapp.entity.TransactionPair;
import com.demo.ticker.tickerapp.tasks.BackgroundCheckTask;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bhusu01
 */
@Service
public class TickerUpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickerUpdateService.class);

    public String helloUniverse() {
        return "Hello Universe";
    }

    final ExecutorService executorService = Executors.newFixedThreadPool(10);
    final Map<UUID, TaskWrapper> taskMap = new HashMap<>();

    public UUID submitTask(TransactionPair pair) {
        UUID taskId = UUID.randomUUID();
        BackgroundCheckTask task = new BackgroundCheckTask(pair);
        Future<?> taskHandle = executorService.submit(task);
        TaskWrapper wrapper = new TaskWrapper(taskHandle, pair);
        taskMap.put(taskId, wrapper);
        return taskId;
    }

    public Map<UUID, TransactionPair> listRunningTasks(){
        Map<UUID, TransactionPair> taskPairs = new HashMap<>();
        for(UUID taskId: taskMap.keySet()) {
            TaskWrapper wrapper = taskMap.get(taskId);
            taskPairs.put(taskId, wrapper.pair);
        }
        return taskPairs;
    }

    public boolean stopTask(UUID taskId) {
        TaskWrapper taskWrapper = taskMap.remove(taskId);
        if(taskWrapper == null) {
            throw new IllegalArgumentException("No task with Id " + taskId);
        }
        Future taskHandle = taskWrapper.taskHandle;
        if(!taskHandle.isDone()) {
            LOGGER.info("Cancelling task {}", taskId);
            boolean status = taskHandle.cancel(true);
            LOGGER.info("Task {} cancelled: {}", taskId, status);
            return status;
        }
        throw new IllegalStateException("The task has already been stopped");
    }
}

@AllArgsConstructor
@Getter
class TaskWrapper {
    final Future taskHandle;
    final TransactionPair pair;

}





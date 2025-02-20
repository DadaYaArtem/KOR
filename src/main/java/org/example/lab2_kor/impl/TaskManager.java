package org.example.lab2_kor.impl;

import org.example.lab2_kor.data.Task;
import org.example.lab2_kor.interfaces.IDeadLetterQueue;
import org.example.lab2_kor.interfaces.ILoggingService;
import org.example.lab2_kor.interfaces.ITaskService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TaskManager implements ITaskService {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final ILoggingService logger;
    private final IDeadLetterQueue dlq;
    private final Random random = new Random();

    public TaskManager(ILoggingService logger, IDeadLetterQueue dlq) {
        this.logger = logger;
        this.dlq = dlq;
    }

    @Override
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
        logger.log("Created task: " + task);
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            dlq.sendToDLQ("Task retrieval failed", "Task with ID " + id + " not found");
            System.err.println("[ERROR]: Task with ID " + id + " not found");
            return null; // Повертаємо null замість кидання винятку
        }

        if (random.nextInt(10) < 3) { // 30% ймовірність тайм-ауту
            dlq.sendToDLQ("Task retrieval failed", "Timeout while fetching task");
            System.err.println("[ERROR]: Timeout while fetching task ID " + id);
            return null;
        }

        return tasks.get(id);
    }


    @Override
    public void updateTask(int id, Task updatedTask) {
        if (tasks.containsKey(id)) {
            tasks.put(id, updatedTask);
            logger.log("Updated task: " + updatedTask);
        } else {
            dlq.sendToDLQ("Task update failed", "Task with ID " + id + " not found");
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            logger.log("Deleted task with ID: " + id);
        } else {
            dlq.sendToDLQ("Task deletion failed", "Task with ID " + id + " not found");
        }
    }
}

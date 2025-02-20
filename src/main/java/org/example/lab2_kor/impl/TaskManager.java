package org.example.lab2_kor.impl;

import org.example.lab2_kor.data.Task;
import org.example.lab2_kor.interfaces.IDeadLetterQueue;
import org.example.lab2_kor.interfaces.ILoggingService;
import org.example.lab2_kor.interfaces.ITaskService;
import org.example.lab2_kor.impl.dql.FileDLQService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TaskManager implements ITaskService {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final ILoggingService logger;
    private final FileDLQService dlq;
    private final Random random = new Random();

    public TaskManager(ILoggingService logger, IDeadLetterQueue dlq) {
        this.logger = logger;
        this.dlq = (FileDLQService) dlq;
    }

    @Override
    public void createTask(Task task) {
        if (task.getTitle().isEmpty()) {
            String message = "[DLQ] Task creation failed | Title cannot be empty";
            dlq.sendToDLQ("Task creation failed", "Title cannot be empty");
            logger.log(message);
            return;
        }

        if (random.nextInt(10) < 2) {
            String message = "[DLQ] Task creation failed | External service unavailable";
            dlq.sendToDLQ("Task creation failed", "External service unavailable");
            logger.log(message);
            return;
        }

        tasks.put(task.getId(), task);
        logger.log("[INFO] Created task: " + task);
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            String message = "[DLQ] Task retrieval failed | Task with ID " + id + " not found";
            dlq.sendToDLQ("Task retrieval failed", "Task with ID " + id + " not found");
            logger.log(message);
            return null;
        }

        if (random.nextInt(10) < 3) {
            String message = "[DLQ] Task retrieval failed | Timeout while fetching task ID " + id;
            dlq.sendToDLQ("Task retrieval failed", "Timeout while fetching task");
            logger.log(message);
            return null;
        }

        logger.log("[INFO] Retrieved task: " + tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void updateTask(int id, Task updatedTask) {
        if (!tasks.containsKey(id)) {
            String message = "[DLQ] Task update failed | Task with ID " + id + " not found";
            dlq.sendToDLQ("Task update failed", "Task with ID " + id + " not found");
            logger.log(message);
            return;
        }

        if (random.nextInt(10) < 2) {
            String message = "[DLQ] Task update failed | Internal server error";
            dlq.sendToDLQ("Task update failed", "Internal server error");
            logger.log(message);
            return;
        }

        tasks.put(id, updatedTask);
        logger.log("[INFO] Updated task: " + updatedTask);
    }

    @Override
    public void deleteTask(int id) {
        if (!tasks.containsKey(id)) {
            String message = "[DLQ] Task deletion failed | Task with ID " + id + " not found";
            dlq.sendToDLQ("Task deletion failed", "Task with ID " + id + " not found");
            logger.log(message);
            return;
        }

        tasks.remove(id);
        logger.log("[INFO] Deleted task with ID: " + id);
    }

    public void printDLQStatistics() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        dlq.printErrorStatistics();
    }

    public void resetDLQStatistics() {
        dlq.resetStatistics();
    }
}

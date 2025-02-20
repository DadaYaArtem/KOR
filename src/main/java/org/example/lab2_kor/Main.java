package org.example.lab2_kor;

import org.example.lab2_kor.data.Task;
import org.example.lab2_kor.impl.*;
import org.example.lab2_kor.impl.dql.FileDLQService;
import org.example.lab2_kor.impl.logging.CompositeLoggingService;
import org.example.lab2_kor.impl.logging.ConsoleLoggingService;
import org.example.lab2_kor.impl.logging.FileLoggingService;
import org.example.lab2_kor.interfaces.IDeadLetterQueue;
import org.example.lab2_kor.interfaces.ILoggingService;
import org.example.lab2_kor.interfaces.ITaskService;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ILoggingService logger = new CompositeLoggingService(List.of(new FileLoggingService(), new ConsoleLoggingService()));
        IDeadLetterQueue dlq = new FileDLQService();
        ITaskService taskService = new TaskManager(logger, dlq);

        // 1. Створюємо завдання
        Task task1 = new Task(1, "Create API", "Develop REST API for service");
        taskService.createTask(task1);

        // 2. Створюємо завдання з пустим заголовком (потрапить у DLQ)
        Task task2 = new Task(2, "", "Invalid task");
        taskService.createTask(task2);

        // 3. Отримуємо завдання (може потрапити в DLQ через таймаут)
        Task foundTask = taskService.getTaskById(1);
        if (foundTask != null) {
            logger.log("[INFO] Retrieved task: " + foundTask);
        } else {
            logger.log("[ERROR] Task not found or an error occurred.");
        }

        // 4. Видаляємо неіснуюче завдання (потрапить у DLQ)
        taskService.deleteTask(99);

        // 5. Чекаємо, щоб всі повторні спроби завершились
        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 6. Виводимо статистику помилок
        ((TaskManager) taskService).printDLQStatistics();

        // 7. Очищуємо статистику
        ((TaskManager) taskService).resetDLQStatistics();
    }
}

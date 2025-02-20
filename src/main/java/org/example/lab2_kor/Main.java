package org.example.lab2_kor;

import org.example.lab2_kor.data.Task;
import org.example.lab2_kor.impl.*;
import org.example.lab2_kor.impl.logging.CompositeLoggingService;
import org.example.lab2_kor.impl.logging.ConsoleLoggingService;
import org.example.lab2_kor.impl.logging.FileLoggingService;
import org.example.lab2_kor.interfaces.IDeadLetterQueue;
import org.example.lab2_kor.interfaces.ILoggingService;
import org.example.lab2_kor.interfaces.ITaskService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ILoggingService logger = new CompositeLoggingService(List.of(new FileLoggingService(), new ConsoleLoggingService()));
        IDeadLetterQueue dlq = new InMemoryDLQ();
        ITaskService taskService = new TaskManager(logger, dlq);

        // Додавання завдань
        Task task1 = new Task(1, "Розробити API", "Створити REST API для сервісу");
        taskService.createTask(task1);

        Task task2 = new Task(2, "Написати документацію", "Додати опис API");
        taskService.createTask(task2);

        // Отримання завдання
        System.out.println("Отримане завдання по id 1: " + taskService.getTaskById(1));

        // Оновлення завдання
        Task updatedTask = new Task(1, "Розробити API", "Оновити REST API");
        taskService.updateTask(1, updatedTask);

        // Видалення завдання
        taskService.deleteTask(2);

        // Спроба видалити неіснуюче завдання (потрапить у DLQ)
        taskService.deleteTask(3);

        // Отримання повідомлень з DLQ
        System.out.println("DLQ Message: " + dlq.retrieveFromDLQ());
    }
}

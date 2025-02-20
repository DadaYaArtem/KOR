package org.example.lab2_kor.interfaces;

import org.example.lab2_kor.data.Task;

public interface ITaskService {
    void createTask(Task task);
    Task getTaskById(int id);
    void updateTask(int id, Task updatedTask);
    void deleteTask(int id);
}

package org.example.lab2_kor.impl.logging;

import org.example.lab2_kor.interfaces.ILoggingService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLoggingService implements ILoggingService {
    private static final String LOG_FILE_PATH = "src/main/resources/logs.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public FileLoggingService() {
        createLogFileIfNotExists();
    }

    private void createLogFileIfNotExists() {
        try {
            File logFile = new File(LOG_FILE_PATH);
            File parentDir = logFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs(); // Створюємо папку resources, якщо її немає
            }
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Не вдалося створити файл логів: " + e.getMessage());
        }
    }

    @Override
    public void log(String message) {
        String timestampedMessage = "[" + LocalDateTime.now().format(formatter) + "] " + message;

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(LOG_FILE_PATH, true), StandardCharsets.UTF_8))) {
            writer.write(timestampedMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[ERROR] Не вдалося записати лог у файл: " + e.getMessage());
        }
    }
}

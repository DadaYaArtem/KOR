package org.example.lab2_kor.impl.dql;

import org.example.lab2_kor.interfaces.IDeadLetterQueue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

public class FileDLQService implements IDeadLetterQueue {
    private static final String DLQ_FILE_PATH = "src/main/resources/dlq_logs.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Queue<String> dlq = new LinkedList<>();

    public FileDLQService() {
        createDLQFileIfNotExists();
    }

    private void createDLQFileIfNotExists() {
        try {
            File logFile = new File(DLQ_FILE_PATH);
            File parentDir = logFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to create DLQ log file: " + e.getMessage());
        }
    }

    @Override
    public void sendToDLQ(String message, String reason) {
        String fullMessage = "[" + LocalDateTime.now().format(formatter) + "] [DLQ] " + message + " | REASON: " + reason;
        dlq.add(fullMessage);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(DLQ_FILE_PATH, true), StandardCharsets.UTF_8))) {
            writer.write(fullMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to write DLQ log: " + e.getMessage());
        }
    }

    @Override
    public String retrieveFromDLQ() {
        return dlq.isEmpty() ? "No messages in DLQ" : dlq.poll();
    }
}

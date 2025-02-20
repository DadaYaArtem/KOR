package org.example.lab2_kor.impl.dql;

import org.example.lab2_kor.interfaces.IDeadLetterQueue;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileDLQService implements IDeadLetterQueue {
    private static final String DLQ_FILE_PATH = "src/main/resources/dlq_logs.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Queue<String> dlq = new LinkedList<>();
    private final Map<String, Integer> errorStats = new HashMap<>();
    private final Map<String, Integer> retryCount = new HashMap<>();
    private static final int MAX_RETRIES = 3;
    private final Queue<String> retryQueue = new LinkedList<>();
    private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1);

    public FileDLQService() {
        createDLQFileIfNotExists();
        startRetryMechanism();
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

    private void startRetryMechanism() {
        retryExecutor.scheduleAtFixedRate(() -> {
            if (!retryQueue.isEmpty()) {
                String message = retryQueue.poll();
                System.out.println("[RETRY] Attempting to process message: " + message);
                sendToDLQ(message, "Retry processing");
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void sendToDLQ(String message, String reason) {
        String timestampedMessage = "[" + LocalDateTime.now().format(formatter) + "] [DLQ] " + message + " | REASON: " + reason;
        errorStats.put(reason, errorStats.getOrDefault(reason, 0) + 1);
        retryCount.put(message, retryCount.getOrDefault(message, 0) + 1);

        if (retryCount.get(message) <= MAX_RETRIES) {
            System.out.println("[RETRY] Scheduling retry for message: " + message + " | Attempt " + retryCount.get(message));
            retryQueue.add(message);
            return;
        }

        dlq.add(timestampedMessage);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(DLQ_FILE_PATH, true), StandardCharsets.UTF_8))) {
            writer.write(timestampedMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to write DLQ log: " + e.getMessage());
        }
    }

    @Override
    public String retrieveFromDLQ() {
        return dlq.isEmpty() ? "No messages in DLQ" : dlq.poll();
    }

    public void printErrorStatistics() {
        System.out.println("\n[DLQ Error Statistics]");
        errorStats.forEach((reason, count) -> System.out.println("Error: " + reason + " | Count: " + count));
    }

    public List<String> getAllErrors() {
        return new ArrayList<>(dlq);
    }

    public void resetStatistics() {
        errorStats.clear();
        retryCount.clear();
        retryQueue.clear();
        System.out.println("[DLQ] Statistics and retry counts have been reset.");
    }
}

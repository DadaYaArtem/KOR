package org.example.lab2_kor.impl.dql;

import org.example.lab2_kor.interfaces.IDeadLetterQueue;

import java.util.LinkedList;
import java.util.Queue;

public class ConsoleDLQService implements IDeadLetterQueue {
    private final Queue<String> dlq = new LinkedList<>();

    @Override
    public void sendToDLQ(String message, String reason) {
        String fullMessage = "[DLQ] " + message + " | REASON: " + reason;
        dlq.add(fullMessage);
        System.err.println(fullMessage);
    }

    @Override
    public String retrieveFromDLQ() {
        return dlq.isEmpty() ? "No messages in DLQ" : dlq.poll();
    }
}

package org.example.lab2_kor.impl;

import org.example.lab2_kor.interfaces.IDeadLetterQueue;

import java.util.LinkedList;
import java.util.Queue;

public class InMemoryDLQ implements IDeadLetterQueue {
    private final Queue<String> dlq = new LinkedList<>();

    @Override
    public void sendToDLQ(String message, String reason) {
        dlq.add("FAILED: " + message + " | REASON: " + reason);
        System.err.println("[DLQ]: " + message + " | REASON: " + reason);
    }

    @Override
    public String retrieveFromDLQ() {
        return dlq.isEmpty() ? "No messages in DLQ" : dlq.poll();
    }
}

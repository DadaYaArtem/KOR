package org.example.lab2_kor.interfaces;

public interface IDeadLetterQueue {
    void sendToDLQ(String message, String reason);
    String retrieveFromDLQ();
}

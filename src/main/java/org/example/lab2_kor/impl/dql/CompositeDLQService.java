package org.example.lab2_kor.impl.dql;

import org.example.lab2_kor.interfaces.IDeadLetterQueue;

import java.util.List;

public class CompositeDLQService implements IDeadLetterQueue {
    private final List<IDeadLetterQueue> dlqServices;

    public CompositeDLQService(List<IDeadLetterQueue> dlqServices) {
        this.dlqServices = dlqServices;
    }

    @Override
    public void sendToDLQ(String message, String reason) {
        for (IDeadLetterQueue dlq : dlqServices) {
            dlq.sendToDLQ(message, reason);
        }
    }

    @Override
    public String retrieveFromDLQ() {
        return dlqServices.get(0).retrieveFromDLQ();
    }
}

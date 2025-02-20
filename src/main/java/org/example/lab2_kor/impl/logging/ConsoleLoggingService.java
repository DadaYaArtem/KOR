package org.example.lab2_kor.impl.logging;

import org.example.lab2_kor.interfaces.ILoggingService;

public class ConsoleLoggingService implements ILoggingService {
    @Override
    public void log(String message) {
        System.out.println("[LOG]: " + message);
    }
}

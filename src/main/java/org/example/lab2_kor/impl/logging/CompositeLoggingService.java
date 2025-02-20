package org.example.lab2_kor.impl.logging;

import org.example.lab2_kor.interfaces.ILoggingService;

import java.util.List;

public class CompositeLoggingService implements ILoggingService {
    private final List<ILoggingService> loggers;

    public CompositeLoggingService(List<ILoggingService> loggers) {
        this.loggers = loggers;
    }

    @Override
    public void log(String message) {
        for (ILoggingService logger : loggers) {
            logger.log(message);
        }
    }
}

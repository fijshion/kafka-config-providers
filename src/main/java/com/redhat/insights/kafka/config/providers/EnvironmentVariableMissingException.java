package com.redhat.insights.kafka.config.providers;

public class EnvironmentVariableMissingException extends RuntimeException {
    private static final long serialVersionUID = 3283717955744967347L;

    public EnvironmentVariableMissingException(String message) {
        super(message);
    }
}

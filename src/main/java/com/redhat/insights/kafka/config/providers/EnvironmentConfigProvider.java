package com.redhat.insights.kafka.config.providers;

import org.apache.kafka.common.config.ConfigData;
import org.apache.kafka.common.config.provider.ConfigProvider;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link ConfigProvider} implementation that reads values from environment variables.
 *
 * Example:
 *
 * "database.password": "${env:DB_PASSWORD}",
 */
public class EnvironmentConfigProvider implements ConfigProvider {

    @Override
    public void configure(Map<String, ?> cfg) {
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public ConfigData get(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfigData get(String path, Set<String> keys) {
        if (path == null || path.length() != 0) {
            throw new IllegalArgumentException("Only basic syntax (e.g. ${env:VARIABLE_NAME}) is supported");
        }

        return new ConfigData(keys.stream().collect(Collectors.toMap(key -> key, key -> {
            String value = System.getenv(key);
            if (value == null) {
                throw new EnvironmentVariableMissingException("Environment variable " + key + " not found");
            }
            return value.trim();
        })));
    }
}

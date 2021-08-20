package com.redhat.insights.kafka.config.providers;

import org.apache.kafka.common.config.ConfigData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import java.io.IOException;
import java.util.Collections;

public class EnvironmentConfigProviderTest {

    private EnvironmentConfigProvider instance;

    @BeforeEach
    public void init() {
        instance = new EnvironmentConfigProvider();
        instance.configure(Collections.emptyMap());
    }

    @AfterEach
    public void close() throws IOException {
        instance.close();
    }

    @Test
    @SetEnvironmentVariable(key = "DATABASE_HOSTNAME", value = "a.test.db")
    public void testReadingEnvironmentVariable() {
        final ConfigData result = instance.get("", Collections.singleton("DATABASE_HOSTNAME"));
        assertEquals("a.test.db", result.data().get("DATABASE_HOSTNAME"));
    }

    @Test
    @SetEnvironmentVariable(key = "DATABASE_HOSTNAME", value = "\na.test.db\n")
    public void testReadingEnvVarWithNewline() {
        final ConfigData result = instance.get("", Collections.singleton("DATABASE_HOSTNAME"));
        assertEquals("a.test.db", result.data().get("DATABASE_HOSTNAME"));
    }

    @Test
    public void testExceptionWhenMissingEnvironmentVariable() {
        assertThrows(EnvironmentVariableMissingException.class, () -> instance.get("", Collections.singleton("key")));
    }

    @Test
    public void testExceptionThrownOnPathPlusKey() {
        assertThrows(IllegalArgumentException.class, () -> instance.get("path", Collections.singleton("key")));
    }
}

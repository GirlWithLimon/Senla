package org.example.bookstore_app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DI {

    private static final Map<Class<?>, Object> configInstances = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(DI.class);
    public static synchronized <T> T getConfig(Class<T> configClass) {
        if (configInstances.containsKey(configClass)) {
            return configClass.cast(configInstances.get(configClass));
        }

        try {
            T config = ConfigurationLoader.loadConfiguration(configClass);
            configInstances.put(configClass, config);
            return config;
        } catch (Exception e) {
            logger.error("Ошибка при загрузке конфигурации: {}", e.getMessage());
            throw new RuntimeException("Не удалось загрузить конфигурацию", e);
        }
    }

    public static synchronized void reloadConfig(Class<?> configClass) {
        configInstances.remove(configClass);
    }

    public static synchronized void reloadAllConfigs() {
        configInstances.clear();
    }
}

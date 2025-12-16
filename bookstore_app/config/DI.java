package bookstore_app.config;

import java.util.HashMap;
import java.util.Map;

public class DI {

    private static final Map<Class<?>, Object> configInstances = new HashMap<>();

    public static synchronized <T> T getConfig(Class<T> configClass) {
        if (configInstances.containsKey(configClass)) {
            return configClass.cast(configInstances.get(configClass));
        }

        try {
            T config = ConfigurationLoader.loadConfiguration(configClass);
            configInstances.put(configClass, config);
            return config;
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке конфигурации: " + e.getMessage());
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
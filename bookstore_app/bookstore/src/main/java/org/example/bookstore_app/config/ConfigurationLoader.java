package org.example.bookstore_app.config;

import org.example.annotation.ConfigProperty;
import org.example.annotation.PropertyType;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigurationLoader {

    private ConfigurationLoader() {}

    public static <T> T loadConfiguration(Class<T> configClass) throws Exception {
        T configInstance = configClass.getDeclaredConstructor().newInstance();

        Set<String> configFiles = new HashSet<>();
        Map<Field, ConfigProperty> fieldAnnotations = new HashMap<>();

        // Собираем все аннотации @ConfigProperty
        for (Field field : configClass.getDeclaredFields()) {
            ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
            if (annotation != null) {
                fieldAnnotations.put(field, annotation);
                configFiles.add(annotation.configFileName());
            }
        }

        // Если нет аннотаций @ConfigProperty, просто возвращаем экземпляр
        if (fieldAnnotations.isEmpty()) {
            System.out.println("В классе " + configClass.getSimpleName() + " нет аннотаций @ConfigProperty");
            return configInstance;
        }

        Properties allProperties = new Properties();
        for (String configFile : configFiles) {
            try (InputStream input = getResourceAsStream(configFile)) {
                if (input != null) {
                    Properties fileProps = new Properties();
                    fileProps.load(input);
                    allProperties.putAll(fileProps);
                    System.out.println("Загружен файл конфигурации: " + configFile);
                } else {
                    System.out.println("Предупреждение: конфигурационный файл " +
                            configFile + " не найден в classpath. Используются значения по умолчанию.");
                }
            } catch (IOException e) {
                System.out.println("Ошибка чтения файла " + configFile + ": " + e.getMessage());
            }
        }

        // Применяем значения из свойств к полям
        for (Map.Entry<Field, ConfigProperty> entry : fieldAnnotations.entrySet()) {
            Field field = entry.getKey();
            ConfigProperty annotation = entry.getValue();

            String propertyKey = annotation.propertyName();
            if (propertyKey.isEmpty()) {
                propertyKey = configClass.getSimpleName().toLowerCase() + "." + field.getName();
            }

            String propertyValue = allProperties.getProperty(propertyKey);
            if (propertyValue != null) {
                setFieldValue(configInstance, field, propertyValue, annotation.type());
                System.out.println("Установлено значение " + propertyKey + " = " + propertyValue);
            } else {
                System.out.println("Свойство " + propertyKey + " не найдено в конфигурации");
            }
        }

        return configInstance;
    }

    private static InputStream getResourceAsStream(String resourcePath) {
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }

        InputStream input = ConfigurationLoader.class.getClassLoader()
                .getResourceAsStream(resourcePath);

        if (input == null) {
            input = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(resourcePath);
        }

        if (input == null) {
            try {
                File file = new File(resourcePath);
                if (file.exists()) {
                    return new FileInputStream(file);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Файл не найден: " + resourcePath);
            }
        }

        return input;
    }

    private static <T> void setFieldValue(T instance, Field field, String value, PropertyType type)
            throws Exception {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();

        try {
            switch (type) {
                case STRING:
                    field.set(instance, value);
                    break;

                case INTEGER:
                    field.set(instance, Integer.parseInt(value.trim()));
                    break;

                case DOUBLE:
                    field.set(instance, Double.parseDouble(value.trim()));
                    break;

                case BOOLEAN:
                    field.set(instance, Boolean.parseBoolean(value.trim()));
                    break;

                case ARRAY:
                    if (fieldType.isArray() && fieldType.getComponentType() == String.class) {
                        String[] array = Arrays.stream(value.split(","))
                                .map(String::trim)
                                .toArray(String[]::new);
                        field.set(instance, array);
                    }
                    break;

                case LIST:
                    if (fieldType == List.class) {
                        List<String> list = Arrays.stream(value.split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                        field.set(instance, list);
                    }
                    break;

                case AUTO:
                default:
                    setFieldValueAuto(instance, field, value);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Ошибка при установке значения поля " + field.getName() +
                    ": " + e.getMessage());
            throw e;
        }
    }

    private static <T> void setFieldValueAuto(T instance, Field field, String value)
            throws Exception {
        Class<?> fieldType = field.getType();

        if (fieldType == String.class) {
            field.set(instance, value);
        } else if (fieldType == int.class || fieldType == Integer.class) {
            field.set(instance, Integer.parseInt(value.trim()));
        } else if (fieldType == double.class || fieldType == Double.class) {
            field.set(instance, Double.parseDouble(value.trim()));
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            field.set(instance, Boolean.parseBoolean(value.trim()));
        } else if (fieldType == String[].class) {
            String[] array = Arrays.stream(value.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);
            field.set(instance, array);
        } else if (fieldType == List.class) {
            List<String> list = Arrays.stream(value.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            field.set(instance, list);
        } else {
            throw new IllegalArgumentException("Неподдерживаемый тип поля: " + fieldType);
        }
    }

    // Дополнительный метод для загрузки конфигурации с пользовательскими свойствами
    public static <T> T loadConfiguration(Class<T> configClass, Properties customProperties) throws Exception {
        T configInstance = configClass.getDeclaredConstructor().newInstance();
        Map<Field, ConfigProperty> fieldAnnotations = new HashMap<>();

        // Собираем все аннотации @ConfigProperty
        for (Field field : configClass.getDeclaredFields()) {
            ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
            if (annotation != null) {
                fieldAnnotations.put(field, annotation);
            }
        }

        if (fieldAnnotations.isEmpty()) {
            return configInstance;
        }

        // Применяем значения из пользовательских свойств
        for (Map.Entry<Field, ConfigProperty> entry : fieldAnnotations.entrySet()) {
            Field field = entry.getKey();
            ConfigProperty annotation = entry.getValue();

            String propertyKey = annotation.propertyName();
            if (propertyKey.isEmpty()) {
                propertyKey = configClass.getSimpleName().toLowerCase() + "." + field.getName();
            }

            String propertyValue = customProperties.getProperty(propertyKey);
            if (propertyValue != null) {
                setFieldValue(configInstance, field, propertyValue, annotation.type());
            }
        }

        return configInstance;
    }
}
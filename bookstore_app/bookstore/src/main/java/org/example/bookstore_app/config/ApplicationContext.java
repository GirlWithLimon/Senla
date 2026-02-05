package org.example.bookstore_app.config;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.annotation.Singleton;
import org.example.bookstore_app.dao.DBConfig;
import org.example.bookstore_app.dao.DBConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

@Component
@Singleton
public class ApplicationContext {//Инициализация
    private static final Logger LOGGER = LoggerFactory
                                        .getLogger(ApplicationContext.class);
    private static ApplicationContext instance;
    private final Map<Class<?>, Object> beans = new HashMap<>();
    private final Set<String> scannedPackages = new HashSet<>();

    private ApplicationContext() {//какие пакеты сканируем
        scannedPackages.add("org.example.bookstore_app");
    }

    public static synchronized ApplicationContext getInstance() {
        //получение
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    public void initialize() {//инициализация
        LOGGER.debug("Начинаем сканирование компонентов...");
        scanAndRegisterComponents();

        createSimpleBeans();
        createAllBeanInstances();
        injectDependencies();

        LOGGER.debug("Инициализация DI контейнера завершена");
        printRegisteredBeans();
    }

    private void scanAndRegisterComponents() { //сканирование и регистрация компонентов
        for (String packageName : scannedPackages) {
            try {
                List<Class<?>> classes = getClasses(packageName);

                for (Class<?> clazz : classes) {
                    if (clazz.isAnnotationPresent(Component.class) ||
                            clazz.isAnnotationPresent(Singleton.class)) {

                        if (!beans.containsKey(clazz)) {
                            LOGGER.debug("Найден компонент: {}", clazz.getSimpleName());
                            beans.put(clazz, null);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Ошибка при сканировании пакета {}: {}", packageName,
                             e.getMessage());
            }
        }
    }

    private void createSimpleBeans() {
        List<Class<?>> simpleClasses = new ArrayList<>();

        for (Class<?> clazz : beans.keySet()) {
            if (beans.get(clazz) == null && hasDefaultConstructor(clazz)) {
                simpleClasses.add(clazz);
            }
        }

        for (Class<?> clazz : simpleClasses) {
            try {
                createBeanWithDefaultConstructor(clazz);
            } catch (Exception e) {
                LOGGER.error("Ошибка при создании простого бина {}: {}",
                            clazz.getSimpleName(), e.getMessage());
            }
        }
    }

    private boolean hasDefaultConstructor(final Class<?> clazz) {
        if (clazz.isInterface()) return false;

        try {
            Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
            return defaultConstructor != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private void createBeanWithDefaultConstructor(Class<?> clazz)
    throws Exception {
        Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
        defaultConstructor.setAccessible(true);
        Object instance = defaultConstructor.newInstance();
        beans.put(clazz, instance);

        LOGGER.debug("Создание простых бинов: {}", clazz.getSimpleName());

        for (Class<?> iface : clazz.getInterfaces()) {
            beans.put(iface, instance);
        }
    }

    private void createAllBeanInstances() {
        // Сначала создаем DBConnect и другие конфигурационные бины
        Class<?>[] priorityClasses = {DBConnect.class,
                                      DBConfig.class,
                                      BookstoreConfig.class};
        for (Class<?> priorityClass : priorityClasses) {
            if (beans.containsKey(priorityClass) &&
                beans.get(priorityClass) == null &&
                !priorityClass.isInterface()) {
                try {
                    LOGGER.debug("Creating priority bean: {}",
                                 priorityClass.getSimpleName());
                    createBeanInstance(priorityClass);
                } catch (Exception e) {
                    LOGGER.error("Ошибка при создании приоритетного бина {}: {}",
                                priorityClass.getSimpleName(), e.getMessage());
                }
            }
        }

        List<Class<?>> classesToCreate = new ArrayList<>();
        for (Class<?> clazz : beans.keySet()) {
            if (beans.get(clazz) == null &&
                !clazz.isInterface()) {
                // Проверяем, не создали ли мы уже этот класс как приоритетный
                if (!Arrays.asList(priorityClasses).contains(clazz)) {
                    classesToCreate.add(clazz);
                }
            }
        }

        LOGGER.debug("DEBUG: Classes to create: {}",
                     classesToCreate.stream().map(Class::getSimpleName).toList());

        // Сортируем по количеству зависимостей (от меньшего к большему)
        classesToCreate.sort(Comparator.comparingInt(this::countDependencies));

        LOGGER.debug("Создаем бины с зависимостями в порядке: {}",
                     classesToCreate.stream().map(Class::getSimpleName).toList());

        for (Class<?> clazz : classesToCreate) {
            try {
                if (beans.get(clazz) == null) {
                    LOGGER.debug("DEBUG: Creating instance of " + clazz.getName());
                    createBeanInstance(clazz);

                    // После создания проверяем, что зависимости внедрены
                    Object bean = beans.get(clazz);
                    if (bean != null) {
                        LOGGER.debug("DEBUG: Successfully created {}",
                                        clazz.getSimpleName());
                        // Логируем состояние @Inject полей
                        checkInjectedFields(bean);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Ошибка при создании экземпляра {}: {}",
                             clazz.getSimpleName(), e.getMessage());
            }
        }
    }

    private void checkInjectedFields(Object bean) {//
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(bean);
                    if (fieldValue == null) {
                        LOGGER.warn("Field " + field.getName() +
                                " in " + clazz.getSimpleName() + " is NULL!");
                    } else {
                        LOGGER.debug("DEBUG: Field " + field.getName() +
                                " in " + clazz.getSimpleName() + " = " +
                                fieldValue.getClass().getSimpleName());
                    }
                } catch (Exception e) {
                    LOGGER.error("DEBUG: Cannot check field " + field.getName() +
                            ": " + e.getMessage());
                }
            }
        }
    }

    private int countDependencies(final Class<?> clazz) {
        int count = 0;

        // Проверяем конструкторы с @Inject
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                return constructor.getParameterCount();
            }
        }

        // Считаем поля с @Inject
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                count++;
            }
        }

        return count;
    }

    private void createBeanInstance( final Class<?> clazz) throws Exception {
        if (!clazz.isInterface()) {
            LOGGER.debug("Создаем экземпляр: {}", clazz.getSimpleName());

            // Ищем конструктор с @Inject
            Constructor<?> injectConstructor = null;
            Constructor<?> defaultConstructor = null;
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();

            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(Inject.class)) {
                    injectConstructor = constructor;
                    break;
                }
                if (constructor.getParameterCount() == 0) {
                    defaultConstructor = constructor;
                }
            }

            Object instance;
            if (injectConstructor != null) {
                LOGGER.debug("DEBUG: Using @Inject constructor for {}",
                        clazz.getSimpleName());
                Class<?>[] paramTypes = injectConstructor.getParameterTypes();
                Object[] params = new Object[paramTypes.length];

                for (int i = 0; i < paramTypes.length; i++) {
                    params[i] = getBean(paramTypes[i]);
                    if (params[i] == null) {
                        params[i] = findBeanByInterface(paramTypes[i]);
                    }

                    if (params[i] == null) {
                        LOGGER.error("Внимание: зависимость не найдена для конструктора {} параметр: {}", clazz.getSimpleName(), paramTypes[i].getSimpleName());
                        // Пытаемся создать дефолтное значение
                        params[i] = getDefaultValue(paramTypes[i]);
                    }
                }

                injectConstructor.setAccessible(true);
                instance = injectConstructor.newInstance(params);
            } else if (defaultConstructor != null) {
                LOGGER.debug("DEBUG: Using default constructor for {}", clazz.getSimpleName());
                defaultConstructor.setAccessible(true);
                instance = defaultConstructor.newInstance();
            } else {
                // Используем первый доступный конструктор
                if (constructors.length > 0) {
                    LOGGER.debug("DEBUG: Using first constructor for {}", clazz.getSimpleName());
                    Constructor<?> constructor = constructors[0];
                    constructor.setAccessible(true);
                    Class<?>[] paramTypes = constructor.getParameterTypes();
                    Object[] params = new Object[paramTypes.length];

                    for (int i = 0; i < paramTypes.length; i++) {
                        params[i] = getBean(paramTypes[i]);
                        if (params[i] == null) {
                            params[i] = findBeanByInterface(paramTypes[i]);
                        }
                        if (params[i] == null) {
                            params[i] = getDefaultValue(paramTypes[i]);
                        }
                    }
                    instance = constructor.newInstance(params);
                } else {
                    throw new RuntimeException("Не найден подходящий конструктор для класса " + clazz.getName());
                }
            }

            beans.put(clazz, instance);

            // Регистрируем интерфейсы
            for (Class<?> iface : clazz.getInterfaces()) {
                beans.put(iface, instance);
            }

            LOGGER.debug("DEBUG: {} added to beans map", clazz.getSimpleName());
        } else {
            return;
        }

    }

    private Object findBeanByInterface(Class<?> interfaceType) {
        for (Map.Entry<Class<?>, Object> entry : beans.entrySet()) {
            if (interfaceType.isAssignableFrom(entry.getKey()) && entry.getValue() != null) {
                return entry.getValue();
            }
        }
        return null;
    }

    private Object getDefaultValue(Class<?> type) {
        if (type == boolean.class || type == Boolean.class) return false;
        if (type == byte.class || type == Byte.class) return (byte) 0;
        if (type == char.class || type == Character.class) return '\0';
        if (type == short.class || type == Short.class) return (short) 0;
        if (type == int.class || type == Integer.class) return 0;
        if (type == long.class || type == Long.class) return 0L;
        if (type == float.class || type == Float.class) return 0.0f;
        if (type == double.class || type == Double.class) return 0.0;
        return null;
    }

    private void injectDependencies() {
        LOGGER.debug("DEBUG: Starting dependency injection");

        List<Object> beanInstances = new ArrayList<>();
        for (Object bean : beans.values()) {
            if (bean != null) {
                beanInstances.add(bean);
                LOGGER.debug("Bean to inject: {}", bean.getClass().getSimpleName());
            }
        }

        for (Object bean : beanInstances) {
            injectFieldDependencies(bean);
        }

        LOGGER.debug("Dependency injection completed");
    }

    private void injectFieldDependencies(Object bean) {
        Class<?> clazz = bean.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                try {
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();

                    if (fieldType.isPrimitive()) {
                        continue;
                    }

                    Object dependency = getBean(fieldType);

                    if (dependency == null) {
                        dependency = findBeanByInterface(fieldType);
                    }

                    if (dependency != null) {
                        field.set(bean, dependency);
                        LOGGER.debug("Injected " + fieldType.getSimpleName() +
                                " into field " + field.getName() + " of " + clazz.getSimpleName());
                    } else {
                        LOGGER.error("Внимание: зависимость не найдена для поля " +
                                field.getName() + " класса " + clazz.getSimpleName() +
                                " типа " + fieldType.getSimpleName());

                        // Для DAO классов, попробуем найти DBConnect
                        if (fieldType == DBConnect.class) {
                            LOGGER.error("DBConnect not found for " + clazz.getSimpleName());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Ошибка при внедрении зависимости в поле " +
                            field.getName() + " класса " + clazz.getSimpleName() + ": " + e.getMessage());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        Object bean = beans.get(type);
        if (bean != null) {
            return (T) bean;
        }

        for (Map.Entry<Class<?>, Object> entry : beans.entrySet()) {
            if (type.isAssignableFrom(entry.getKey()) && entry.getValue() != null) {
                return (T) entry.getValue();
            }
        }

        return null;
    }

    public void registerBean(Class<?> type, Object instance) {
        beans.put(type, instance);

        for (Class<?> iface : type.getInterfaces()) {
            beans.put(iface, instance);
        }

        LOGGER.debug("Registered bean " + type.getSimpleName());
    }

    private List<Class<?>> getClasses(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);

        if (resource != null) {
            File directory = new File(resource.getFile());
            if (directory.exists()) {
                classes.addAll(findClasses(directory, packageName));
            }
        }

        return classes;
    }

    private List<Class<?>> findClasses(File directory, String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);
                    } catch (ClassNotFoundException e) {
                        LOGGER.error("Не удалось загрузить класс: " + className);
                    } catch (NoClassDefFoundError e) {
                        LOGGER.error("Ошибка загрузки класса " + className + ": " + e.getMessage());
                    }
                }
            }
        }

        return classes;
    }

    public void printRegisteredBeans() {
        System.out.println("\n=== Зарегистрированные компоненты ===");
        int count = 0;
        int nullCount = 0;

        // Сначала выводим созданные бины
        LOGGER.debug("\n--- Созданные бины ---");
        for (Map.Entry<Class<?>, Object> entry : beans.entrySet()) {
            if (entry.getValue() != null) {
                LOGGER.debug("  " + entry.getKey().getSimpleName() + " -> " +
                        entry.getValue().getClass().getSimpleName() + " (INSTANCE)");
                count++;
            } else {
                nullCount++;
            }
        }

        if (nullCount > 0) {
            LOGGER.debug("\n--- Не созданные бины (NULL) ---");
            for (Map.Entry<Class<?>, Object> entry : beans.entrySet()) {
                if (entry.getValue() == null) {
                    LOGGER.debug("  " + entry.getKey().getSimpleName() + " -> NULL");
                }
            }
        }

        LOGGER.debug("\nВсего экземпляров: " + count + " из " + beans.size());
        LOGGER.debug("NULL компонентов: " + nullCount);

        // Дополнительная проверка для DBConnect
        DBConnect dbConnect = getBean(DBConnect.class);
        if (dbConnect == null) {
            LOGGER.error("Критическая ошибка: DBConnect is NULL!");
        } else {
            LOGGER.debug("✓ DBConnect initialized successfully");
        }
    }
}
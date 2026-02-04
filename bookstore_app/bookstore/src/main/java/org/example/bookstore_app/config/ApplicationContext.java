package org.example.bookstore_app.config;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.annotation.Singleton;
import org.example.bookstore_app.dao.DBConfig;
import org.example.bookstore_app.dao.DBConnect;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

@Component
@Singleton
public class ApplicationContext {

    private static ApplicationContext instance;
    private final Map<Class<?>, Object> beans = new HashMap<>();
    private final Set<String> scannedPackages = new HashSet<>();

    private ApplicationContext() {
        scannedPackages.add("org.example.bookstore_app");
    }

    public static synchronized ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    public void initialize() {
        System.out.println("Начинаем сканирование компонентов...");
        scanAndRegisterComponents();

        createSimpleBeans();
        createAllBeanInstances();
        injectDependencies();

        System.out.println("Инициализация DI контейнера завершена");
        printRegisteredBeans();
    }

    private void scanAndRegisterComponents() {
        for (String packageName : scannedPackages) {
            try {
                List<Class<?>> classes = getClasses(packageName);

                for (Class<?> clazz : classes) {
                    if (clazz.isAnnotationPresent(Component.class) ||
                            clazz.isAnnotationPresent(Singleton.class)) {

                        if (!beans.containsKey(clazz)) {
                            System.out.println("Найден компонент: " + clazz.getSimpleName());
                            beans.put(clazz, null);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Ошибка при сканировании пакета " + packageName + ": " + e.getMessage());
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
                System.err.println("Ошибка при создании простого бина " + clazz.getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    private boolean hasDefaultConstructor(Class<?> clazz) {
        if (clazz.isInterface()) return false;

        try {
            Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
            return defaultConstructor != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private void createBeanWithDefaultConstructor(Class<?> clazz) throws Exception {
        Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
        defaultConstructor.setAccessible(true);
        Object instance = defaultConstructor.newInstance();
        beans.put(clazz, instance);

        System.out.println("DEBUG: Created simple bean: " + clazz.getSimpleName());

        for (Class<?> iface : clazz.getInterfaces()) {
            beans.put(iface, instance);
        }
    }

    private void createAllBeanInstances() {
        // Сначала создаем DBConnect и другие конфигурационные бины
        Class<?>[] priorityClasses = {DBConnect.class, DBConfig.class, BookstoreConfig.class};
        for (Class<?> priorityClass : priorityClasses) {
            if (beans.containsKey(priorityClass) && beans.get(priorityClass) == null && !priorityClass.isInterface()) {
                try {
                    System.out.println("DEBUG: Creating priority bean: " + priorityClass.getSimpleName());
                    createBeanInstance(priorityClass);
                } catch (Exception e) {
                    System.err.println("Ошибка при создании приоритетного бина " +
                            priorityClass.getSimpleName() + ": " + e.getMessage());
                }
            }
        }

        List<Class<?>> classesToCreate = new ArrayList<>();
        for (Class<?> clazz : beans.keySet()) {
            if (beans.get(clazz) == null && !clazz.isInterface()) {
                // Проверяем, не создали ли мы уже этот класс как приоритетный
                if (!Arrays.asList(priorityClasses).contains(clazz)) {
                    classesToCreate.add(clazz);
                }
            }
        }

        System.out.println("DEBUG: Classes to create: " +
                classesToCreate.stream().map(Class::getSimpleName).toList());

        // Сортируем по количеству зависимостей (от меньшего к большему)
        classesToCreate.sort(Comparator.comparingInt(this::countDependencies));

        System.out.println("Создаем бины с зависимостями в порядке: " +
                classesToCreate.stream().map(Class::getSimpleName).toList());

        for (Class<?> clazz : classesToCreate) {
            try {
                if (beans.get(clazz) == null) {
                    System.out.println("DEBUG: Creating instance of " + clazz.getName());
                    createBeanInstance(clazz);

                    // После создания проверяем, что зависимости внедрены
                    Object bean = beans.get(clazz);
                    if (bean != null) {
                        System.out.println("DEBUG: Successfully created " + clazz.getSimpleName());
                        // Логируем состояние @Inject полей
                        checkInjectedFields(bean);
                    }
                }
            } catch (Exception e) {
                System.err.println("Ошибка при создании экземпляра " + clazz.getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void checkInjectedFields(Object bean) {
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(bean);
                    if (fieldValue == null) {
                        System.out.println("WARNING: Field " + field.getName() +
                                " in " + clazz.getSimpleName() + " is NULL!");
                    } else {
                        System.out.println("DEBUG: Field " + field.getName() +
                                " in " + clazz.getSimpleName() + " = " +
                                fieldValue.getClass().getSimpleName());
                    }
                } catch (Exception e) {
                    System.err.println("DEBUG: Cannot check field " + field.getName() +
                            ": " + e.getMessage());
                }
            }
        }
    }

    private int countDependencies(Class<?> clazz) {
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

    private void createBeanInstance(Class<?> clazz) throws Exception {
        if (clazz.isInterface()) {
            return;
        }

        System.out.println("Создаем экземпляр: " + clazz.getSimpleName());

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
            System.out.println("DEBUG: Using @Inject constructor for " + clazz.getSimpleName());
            Class<?>[] paramTypes = injectConstructor.getParameterTypes();
            Object[] params = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                params[i] = getBean(paramTypes[i]);
                if (params[i] == null) {
                    params[i] = findBeanByInterface(paramTypes[i]);
                }

                if (params[i] == null) {
                    System.err.println("Внимание: зависимость не найдена для конструктора " +
                            clazz.getSimpleName() + " параметр: " + paramTypes[i].getSimpleName());
                    // Пытаемся создать дефолтное значение
                    params[i] = getDefaultValue(paramTypes[i]);
                }
            }

            injectConstructor.setAccessible(true);
            instance = injectConstructor.newInstance(params);
        } else if (defaultConstructor != null) {
            System.out.println("DEBUG: Using default constructor for " + clazz.getSimpleName());
            defaultConstructor.setAccessible(true);
            instance = defaultConstructor.newInstance();
        } else {
            // Используем первый доступный конструктор
            if (constructors.length > 0) {
                System.out.println("DEBUG: Using first constructor for " + clazz.getSimpleName());
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

        System.out.println("DEBUG: " + clazz.getSimpleName() + " added to beans map");
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
        System.out.println("DEBUG: Starting dependency injection");

        List<Object> beanInstances = new ArrayList<>();
        for (Object bean : beans.values()) {
            if (bean != null) {
                beanInstances.add(bean);
                System.out.println("DEBUG: Bean to inject: " + bean.getClass().getSimpleName());
            }
        }

        for (Object bean : beanInstances) {
            injectFieldDependencies(bean);
        }

        System.out.println("DEBUG: Dependency injection completed");
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
                        System.out.println("DEBUG: Injected " + fieldType.getSimpleName() +
                                " into field " + field.getName() + " of " + clazz.getSimpleName());
                    } else {
                        System.err.println("Внимание: зависимость не найдена для поля " +
                                field.getName() + " класса " + clazz.getSimpleName() +
                                " типа " + fieldType.getSimpleName());

                        // Для DAO классов, попробуем найти DBConnect
                        if (fieldType == DBConnect.class) {
                            System.err.println("ERROR: DBConnect not found for " + clazz.getSimpleName());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка при внедрении зависимости в поле " +
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

        System.out.println("DEBUG: Registered bean " + type.getSimpleName());
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
                        System.err.println("Не удалось загрузить класс: " + className);
                    } catch (NoClassDefFoundError e) {
                        System.err.println("Ошибка загрузки класса " + className + ": " + e.getMessage());
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
        System.out.println("\n--- Созданные бины ---");
        for (Map.Entry<Class<?>, Object> entry : beans.entrySet()) {
            if (entry.getValue() != null) {
                System.out.println("  " + entry.getKey().getSimpleName() + " -> " +
                        entry.getValue().getClass().getSimpleName() + " (INSTANCE)");
                count++;
            } else {
                nullCount++;
            }
        }

        // Затем выводим не созданные
        if (nullCount > 0) {
            System.out.println("\n--- Не созданные бины (NULL) ---");
            for (Map.Entry<Class<?>, Object> entry : beans.entrySet()) {
                if (entry.getValue() == null) {
                    System.out.println("  " + entry.getKey().getSimpleName() + " -> NULL");
                }
            }
        }

        System.out.println("\nВсего экземпляров: " + count + " из " + beans.size());
        System.out.println("NULL компонентов: " + nullCount);

        // Дополнительная проверка для DBConnect
        DBConnect dbConnect = getBean(DBConnect.class);
        if (dbConnect == null) {
            System.err.println("CRITICAL: DBConnect is NULL!");
        } else {
            System.out.println("✓ DBConnect initialized successfully");
        }
    }
}
package org.example.bookstore_app.config;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.annotation.Singleton;

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

    private void createAllBeanInstances() {
        List<Class<?>> classesToCreate = new ArrayList<>();
        for (Class<?> clazz : beans.keySet()) {
            if (beans.get(clazz) == null) {
                classesToCreate.add(clazz);
            }
        }

        for (Class<?> clazz : classesToCreate) {
            try {
                createBeanInstance(clazz);
            } catch (Exception e) {
                System.err.println("Ошибка при создании экземпляра " + clazz.getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    private void createBeanInstance(Class<?> clazz) throws Exception {
        if (clazz.isInterface()) {
            return;
        }

        System.out.println("Создаем экземпляр: " + clazz.getSimpleName());

        Constructor<?> injectConstructor = null;
        Constructor<?> defaultConstructor = null;

        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
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
            Class<?>[] paramTypes = injectConstructor.getParameterTypes();
            Object[] params = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                params[i] = getBean(paramTypes[i]);
                if (params[i] == null) {
                    System.err.println("Внимание: зависимость не найдена для конструктора " +
                            clazz.getSimpleName() + " параметр: " + paramTypes[i].getSimpleName());
                }
            }

            injectConstructor.setAccessible(true);
            instance = injectConstructor.newInstance(params);
        } else if (defaultConstructor != null) {
            defaultConstructor.setAccessible(true);
            instance = defaultConstructor.newInstance();
        } else {
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length > 0) {
                constructors[0].setAccessible(true);
                Class<?>[] paramTypes = constructors[0].getParameterTypes();
                Object[] params = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    params[i] = getDefaultValue(paramTypes[i]);
                }
                instance = constructors[0].newInstance(params);
            } else {
                throw new RuntimeException("Не найден подходящий конструктор для класса " + clazz.getName());
            }
        }

        beans.put(clazz, instance);

        for (Class<?> iface : clazz.getInterfaces()) {
            beans.put(iface, instance);
        }
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
        List<Object> beanInstances = new ArrayList<>();
        for (Object bean : beans.values()) {
            if (bean != null) {
                beanInstances.add(bean);
            }
        }

        for (Object bean : beanInstances) {
            injectFieldDependencies(bean);
        }
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

                    if (dependency != null) {
                        field.set(bean, dependency);
                    } else {
                        System.err.println("Внимание: зависимость не найдена для поля " +
                                field.getName() + " класса " + clazz.getSimpleName() +
                                " типа " + fieldType.getSimpleName());
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

         try {
            boolean isComponent = false;
            for (Class<?> clazz : beans.keySet()) {
                if (clazz.isAssignableFrom(type)) {
                    isComponent = true;
                    break;
                }
            }

            if (isComponent && !type.isInterface()) {
                System.out.println("Динамическое создание бина: " + type.getSimpleName());
                createBeanInstance(type);
                return (T) beans.get(type);
            }
        } catch (Exception e) {
            System.err.println("Не удалось создать bean для типа: " + type.getName() + " - " + e.getMessage());
        }

        return null;
    }

    public void registerBean(Class<?> type, Object instance) {
        beans.put(type, instance);

        for (Class<?> iface : type.getInterfaces()) {
            beans.put(iface, instance);
        }
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
        for (Class<?> clazz : beans.keySet()) {
            Object bean = beans.get(clazz);
            System.out.println("  " + clazz.getSimpleName() + " -> " +
                    (bean != null ? bean.getClass().getSimpleName() + " (INSTANCE)" : "NULL"));
            if (bean != null) count++;
        }
        System.out.println("Всего экземпляров: " + count + " из " + beans.size());
    }
}
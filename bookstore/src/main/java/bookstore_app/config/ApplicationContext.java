package bookstore_app.config;

import bookstore_app.config.annotation.Component;
import bookstore_app.config.annotation.Inject;
import bookstore_app.config.annotation.Singleton;
import bookstore_app.controller.*;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component @Singleton
public class ApplicationContext {

    private static ApplicationContext instance;
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    private final Map<String, Object> namedComponents = new ConcurrentHashMap<>();
    private final Set<Object> allBeans = new HashSet<>();
    private boolean initialized = false;

    private ApplicationContext() {}

    public static synchronized ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    public void initialize() {
        if (initialized) {
            return;
        }

        registerCoreBeans();

        registerComponents();
        injectAllDependencies();

        initialized = true;
    }

    private void registerCoreBeans() {
        try {
            ImportExportService importExportService = new ImportExportService();
            registerBean(ImportExportService.class, importExportService);

        } catch (Exception e) {
            System.err.println("Ошибка при регистрации основных бинов: " + e.getMessage());
        }
    }

    private void registerComponents() {
       try {
            ShowBook showBook = new ShowBook();
            ShowOrdersAndRequests showOrdersAndRequests = new ShowOrdersAndRequests();
            BooksController booksController = new BooksController();
            OrdersController ordersController = new OrdersController();
            OperationController operationController = new OperationController();

            registerBean(ShowBook.class, showBook);
            registerBean(ShowOrdersAndRequests.class, showOrdersAndRequests);
            registerBean(BooksController.class, booksController);
            registerBean(OrdersController.class, ordersController);
            registerBean(OperationController.class, operationController);

            registerBean(IShowBook.class, showBook);
            registerBean(IShowOrdersAndRequests.class, showOrdersAndRequests);
            registerBean(IBookStok.class, booksController);
            registerBean(IOrderOperation.class, ordersController);

        } catch (Exception e) {
            System.err.println("Ошибка при регистрации компонентов: " + e.getMessage());
        }
    }

    private void injectAllDependencies() {
        for (Object bean : allBeans) {
            injectDependencies(bean);
        }
    }

    public <T> void registerBean(Class<T> type, T instance) {
        singletons.put(type, instance);
        allBeans.add(instance);

       for (Class<?> interfaceClass : type.getInterfaces()) {
            singletons.put(interfaceClass, instance);
        }
    }

    public <T> void registerBean(String name, T instance) {
        namedComponents.put(name, instance);
        allBeans.add(instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        Object bean = singletons.get(type);

        if (bean != null) {
            return (T) bean;
        }

        for (Map.Entry<Class<?>, Object> entry : singletons.entrySet()) {
            if (type.isAssignableFrom(entry.getKey())) {
                return (T) entry.getValue();
            }
        }


        if (type.isAnnotationPresent(Component.class) ||
                type.isAnnotationPresent(Singleton.class)) {
            try {
                T newBean = createBean(type);
                registerBean(type, newBean);
                injectDependencies(newBean);
                return newBean;
            } catch (Exception e) {
                throw new RuntimeException("Не удалось создать bean " + type.getName(), e);
            }
        }

        return null;
    }

    private <T> T createBean(Class<T> type) throws Exception {
        return type.getDeclaredConstructor().newInstance();
    }

    private void injectDependencies(Object bean) {
        Class<?> clazz = bean.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();

                Object dependency = getBean(fieldType);
                if (dependency != null) {
                    try {
                        field.set(bean, dependency);
                    } catch (IllegalAccessException e) {
                        System.err.println("Ошибка внедрения зависимости в поле " +
                                field.getName() + " класса " + clazz.getName() + ": " + e.getMessage());
                    }
                } else {
                    System.err.println("Предупреждение: не удалось найти зависимость для поля "
                            + field.getName() + " типа " + fieldType.getName()
                            + " в классе " + clazz.getName());
                }
            }
        }
    }

    public void printRegisteredBeans() {
        System.out.println("\n=== Зарегистрированные компоненты ===");
        for (Map.Entry<Class<?>, Object> entry : singletons.entrySet()) {
            System.out.println(entry.getKey().getSimpleName() + " -> " +
                    entry.getValue().getClass().getSimpleName());
        }
    }
}
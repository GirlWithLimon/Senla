package bookstore_app.config;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component @Singleton
public class ApplicationContext {

    private static ApplicationContext instance;
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    private final Map<String, Object> namedComponents = new ConcurrentHashMap<>();

    private ApplicationContext() {}

    public static synchronized ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
            instance.initialize();
        }
        return instance;
    }

    private void initialize() {
        // 1. Сам себя регистрируем
        singletons.put(ApplicationContext.class, this);

        // 2. Регистрируем конфигурацию
        BookstoreConfig config = ConfigurationLoader.loadConfiguration(BookstoreConfig.class);
        singletons.put(BookstoreConfig.class, config);

        // 3. Автосканирование и создание компонентов
        scanAndCreateComponents();

        // 4. Внедрение зависимостей
        injectDependencies();
    }

    public <T> T getBean(Class<T> type) {
        Object bean = singletons.get(type);
        if (bean != null) {
            return type.cast(bean);
        }

        // Попробуем найти по реализации интерфейса
        for (Map.Entry<Class<?>, Object> entry : singletons.entrySet()) {
            if (type.isAssignableFrom(entry.getKey())) {
                return type.cast(entry.getValue());
            }
        }

        return null;
    }

    public <T> T getBean(String name) {
        return (T) namedComponents.get(name);
    }

    private void scanAndCreateComponents() {
        // Здесь будет сканирование пакетов
        // Для начала создадим вручную основные компоненты

        // Создаём хранилище
        DataSave dataSave = new DataSave();
        Stok stok = dataSave.loadState();
        singletons.put(DataSave.class, dataSave);
        singletons.put(Stok.class, stok);

        // Создаём сервисы
        singletons.put(ShowBook.class, new ShowBook(stok));
        singletons.put(ShowOrdersAndRequests.class, new ShowOrdersAndRequests(stok));
        singletons.put(BooksController.class, new BooksController(stok));
        singletons.put(OrdersController.class, new OrdersController(stok));
        singletons.put(ImportExportService.class, new ImportExportService(stok));

        // Создаём главный контроллер
        singletons.put(OperationController.class, new OperationController());
    }

    private void injectDependencies() {
        for (Object bean : singletons.values()) {
            injectFields(bean);
        }
    }

    private void injectFields(Object bean) {
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
                        System.err.println("Ошибка при внедрении зависимости: " + e.getMessage());
                    }
                }
            }
        }
    }

    public void registerBean(Class<?> type, Object instance) {
        singletons.put(type, instance);
    }
}

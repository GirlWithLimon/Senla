package org.example.bookstore_app.view;

import org.example.bookstore_app.config.SpringConfig;
import org.example.bookstore_app.controller.OperationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationInitializer {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationInitializer.class);
    private static ApplicationContext springContext;

    public static ApplicationContext initialize() {
        logger.debug("Инициализация Spring контекста...");

        // Создаем Spring контекст на основе Java конфигурации
        springContext = new AnnotationConfigApplicationContext(SpringConfig.class);

        logger.debug("Spring контекст инициализирован");

        // Получаем и инициализируем контроллер
        OperationController controller = springContext.getBean(OperationController.class);
        if (controller != null) {
            logger.debug("OperationController получен, инициализируем тестовые данные");
            controller.initializeTestData();
        } else {
            logger.error("Ошибка: не удалось получить OperationController");
        }

        logger.debug("Инициализация завершена");
        return springContext;
    }

    public static ApplicationContext getSpringContext() {
        return springContext;
    }
}
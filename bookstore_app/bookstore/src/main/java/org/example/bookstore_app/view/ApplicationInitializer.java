package org.example.bookstore_app.view;

import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.config.ConfigurationLoader;
import org.example.bookstore_app.controller.DataSave;
import org.example.bookstore_app.controller.OperationController;
import org.example.bookstore_app.dao.DBConfig;
import org.example.bookstore_app.dao.DBConnect;
import org.example.bookstore_app.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ApplicationInitializer {
   private static final Logger logger = LoggerFactory
                                        .getLogger(ApplicationInitializer.class);
   public static ApplicationContext initialize() {
      logger.debug("Инициализация приложения...");
      ApplicationContext context = ApplicationContext.getInstance();

      BookstoreConfig config = loadConfiguration(BookstoreConfig.class);
      context.registerBean(BookstoreConfig.class, config);
      logger.debug("Конфигурация зарегистрирована");
//      DBConfig dbConfig = loadConfiguration(DBConfig.class);
//      context.registerBean(DBConfig.class, dbConfig);
//      logger.debug("Конфигурация зарегистрирована");
//
//      DBConnect conn =  DBConnect.getInstance();
//      context.registerBean(DBConnect.class, conn);
//      logger.debug("DBConnect загружен");

      StockService stock = StockService.getInstance();
      context.registerBean(StockService.class, stock);
      logger.debug("Stok загружен");

      DataSave dataSave = DataSave.getInstance();
      context.registerBean(DataSave.class, dataSave);
      logger.debug("DataSave зарегистрирован");


      context.initialize();

      OperationController controller = context.getBean(OperationController.class);
      if (controller != null) {
         logger.debug("OperationController получен, инициализируем тестовые данные");
         controller.initializeTestData();
      } else {
         logger.error("Ошибка: не удалось получить OperationController");
         context.printRegisteredBeans();
      }

      logger.debug("Инициализация завершена");
      return context;
    }

   private static <T> T loadConfiguration(Class <T> configClass) {
      try {
         return ConfigurationLoader.loadConfiguration(configClass);
      } catch (Exception e) {
         logger.warn("Ошибка загрузки конфигурации: {}" +
                    "Используются значения по умолчанию", e.getMessage());
         return null;
        }
    }
}

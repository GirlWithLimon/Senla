package org.example.bookstore_app.controller;

import org.example.bookstore_app.dao.*;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.service.StockService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSave {
    private static final Logger logger = LoggerFactory.getLogger(DataSave.class);

    @Autowired
    StockService stockService;

    @Transactional(readOnly = true)
    public boolean checkDataExists() {
        try {
            return !stockService.getBooks().isEmpty();
        } catch (Exception e) {
            logger.error("Ошибка при проверке данных: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public void saveState() {
        logger.info("Сохраняем состояние в базу данных...");
        // Данные уже сохранены через сервисы
        logger.info("Состояние сохранено в БД");
    }

    @Transactional(readOnly = true)
    public void loadDate() {
        logger.info("Данные загружены из БД");
        logger.info("Всего загружено: " + stockService.getBooks().size() + " книг, " +
                stockService.getOrders().size() + " заказов, " +
                stockService.getRequests().size() + " запросов");
    }
}
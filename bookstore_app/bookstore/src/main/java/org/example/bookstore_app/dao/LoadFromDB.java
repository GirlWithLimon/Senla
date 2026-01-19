package org.example.bookstore_app.dao;

import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.controller.*;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.Stok;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

public class LoadFromDB {
    public  boolean initialize() {
        System.out.println("Выполняем подключение к базе данных");
        try (Connection conn = DBConnect.getInstance().getConnection()) {
            if (!tablesExist(conn)) {
                System.out.println("БД пуста");
             //   createTables(conn);
             //   insertInitialData(conn);
                return false;
            } else {
                System.out.println("Выполнено подключение к базе данных");
                loadDate(conn);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Ошибка подключения к базе данных: " + e.getMessage());
            return false;
        }
    }

    private boolean tablesExist(Connection conn) throws SQLException {
        String checkSql = "SELECT EXISTS (SELECT 1 FROM information_schema.tables " +
                "WHERE table_schema = 'public' AND table_name = 'book')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next()) {
                return rs.getBoolean(1); // true если таблица существует
            }
            return false;
        }
    }
    private void loadDate(Connection conn){
        try {
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.findAll();

            System.out.println("Найдено книг в БД: " + books.size());

            // Очищаем текущие данные перед загрузкой из БД
            // Это нужно для предотвращения дублирования
            ApplicationContext context = ApplicationContext.getInstance();
            Stok stok = context.getBean(Stok.class);
            if (stok != null) {
                // Получаем OperationController для корректного добавления книг
                OperationController operationController = context.getBean(OperationController.class);
                if (operationController != null) {
                    for(Book book : books){
                        System.out.println("Загружаем книгу: " + book.getName() + " (ID: " + book.getId() + ")");
                        // Используем метод OperationController вместо статического вызова
                        operationController.addBookToStock(book.getName(), book.getAuthor(),
                                book.getPrice(), book.getPublicationDate(),
                                LocalDate.now());
                    }
                }
            }


        } catch (Exception e) {
            System.out.println("Ошибка при загрузке данных из БД: " + e.getMessage());
            e.printStackTrace();
        }

    }
}

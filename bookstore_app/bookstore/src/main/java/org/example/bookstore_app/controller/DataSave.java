package org.example.bookstore_app.controller;

import org.example.annotation.Inject;
import org.example.bookstore_app.dao.*;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.dao.StokService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

public class DataSave {
        @Inject
        DBConnect dbConnect;
        private static final Logger logger = Logger.getLogger(DataSave.class.getName());
        private static final String DATA_FILE = "bookstore_data.out";
        private static DataSave instance;

        public DataSave() {}
        public  boolean initialize() {
            System.out.println("Выполняем подключение к базе данных");
            try (Connection conn = dbConnect.getConnection()) {
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
        public static DataSave getInstance() {
            if (instance == null) {
                instance = new DataSave();
            }
            return instance;
        }

        public void saveState(StokService stokService, Connection conn) throws Exception {
            if(tablesExist(conn)){

            }
        }

    public StokService loadDate(Connection conn) {
        try {
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.findAll();
            System.out.println("Найдено книг в БД: " + books.size());

            StokService stokService = new StokService();

            for(Book book : books){
                System.out.println("Загружаем книгу: " + book.getName() + " (ID: " + book.getId() + ")");
                stokService.addBook(book);

                BookCopyDAO bookCopyDAO = new BookCopyDAO();
                List<BookCopy> copies = bookCopyDAO.findByBookId(book.getId());
                for(BookCopy copy : copies) {
                    stokService.addBooksCopy(copy);
                    book.setStatusStok();
                }
            }
            return stokService;
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке данных из БД: " + e.getMessage());
            e.printStackTrace();
            return new StokService();
        }
    }

        private boolean tablesExist(Connection conn) throws SQLException {
            String checkSql = "SELECT EXISTS (SELECT 1 FROM information_schema.tables " +
                    "WHERE table_schema = 'public' AND table_name = 'book')";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkSql)) {

                if (rs.next()) {
                    return rs.getBoolean(1);
                }
                return false;
            }
        }
}


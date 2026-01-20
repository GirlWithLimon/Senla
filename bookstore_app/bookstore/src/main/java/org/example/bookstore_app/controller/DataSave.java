package org.example.bookstore_app.controller;

import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.dao.*;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.model.Stok;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class DataSave {
        private static final Logger logger = Logger.getLogger(DataSave.class.getName());
        private static final String DATA_FILE = "bookstore_data.out";
        private static DataSave instance;

        public DataSave() {}
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
        public static DataSave getInstance() {
            if (instance == null) {
                instance = new DataSave();
            }
            return instance;
        }

        public void saveState(Stok stok, Connection conn) throws Exception {
            if(tablesExist(conn)){

            }
        }

    public Stok loadDate(Connection conn) {
        try {
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.findAll();
            System.out.println("Найдено книг в БД: " + books.size());

            Stok stok = new Stok();

            for(Book book : books){
                System.out.println("Загружаем книгу: " + book.getName() + " (ID: " + book.getId() + ")");
                stok.addBook(book);

                BookCopyDAO bookCopyDAO = new BookCopyDAO();
                List<BookCopy> copies = bookCopyDAO.findByBookId(book.getId());
                for(BookCopy copy : copies) {
                    stok.addBooksCopy(copy);
                    book.setStatusStok();
                }
            }
            return stok;
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке данных из БД: " + e.getMessage());
            e.printStackTrace();
            return new Stok();
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


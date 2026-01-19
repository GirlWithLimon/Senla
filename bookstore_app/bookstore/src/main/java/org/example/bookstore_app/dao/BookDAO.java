package org.example.bookstore_app.dao;

import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDAO implements GenericDAO<Book, Integer> {
    // Все литералы в константах
    private static final String TABLE_NAME = "book";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_PUBLICATION_DATE = "publicationDate";
    private static final String COLUMN_INFORMATION = "information";
    private static final String COLUMN_STATUS = "status";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_NAME;
    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME + " (" + COLUMN_NAME + ", " + COLUMN_AUTHOR + ", " +
                    COLUMN_PRICE + ", " + COLUMN_PUBLICATION_DATE + ", " + COLUMN_INFORMATION + ", " +
                    COLUMN_STATUS + ") VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE " + TABLE_NAME + " SET " + COLUMN_NAME + " = ?, " + COLUMN_AUTHOR + " = ?, " +
                    COLUMN_PRICE + " = ?, " + COLUMN_PUBLICATION_DATE + " = ?, " + COLUMN_INFORMATION + " = ?, " +
                    COLUMN_STATUS + " = ? WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

    @Override
    public Optional<Book> findById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToBook(rs));
            }
            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
            return books;

        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Book save(Book book) {
        Book book1 = this.<Book>executeInTransaction(conn -> {
            if (book.getId() == 0) {
                return insertBook(conn, book);
            } else {
                updateBook(conn, book);
                return book;
            }
        });
        return book1;
    }

    private Book insertBook(Connection conn, Book book) throws SQLException{
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT,
                Statement.RETURN_GENERATED_KEYS)) {

            setBookParameters(stmt, book);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {

            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1));
                } else {

                }
            }
            return book;
        }
    }

    @Override
    public void update(Book book) {
        executeInTransaction((TransactionOperation) conn -> updateBook(conn, book));
    }

    private void updateBook(Connection conn, Book book) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            setBookParameters(stmt, book);
            stmt.setInt(7, book.getId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {

            }
        }
    }

    @Override
    public void delete(Integer id)  {
        executeInTransaction(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
                stmt.setInt(1, id);
                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {

                }
            }
        });
    }

    // Приватные вспомогательные методы
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book(
                rs.getInt(COLUMN_ID),
                rs.getString(COLUMN_NAME),
                rs.getString(COLUMN_AUTHOR),
                rs.getDouble(COLUMN_PRICE),
                rs.getString(COLUMN_INFORMATION),
                rs.getDate(COLUMN_PUBLICATION_DATE).toLocalDate()
        );

        String statusStr = rs.getString(COLUMN_STATUS);
        if (BookStatus.IN_STOCK.name().equals(statusStr)) {
            book.setStatusStok();
        } else {
            book.setStatusNo();
        }

        return book;
    }

    private void setBookParameters(PreparedStatement stmt, Book book) throws SQLException {
        stmt.setString(1, book.getName());
        stmt.setString(2, book.getAuthor());
        stmt.setDouble(3, book.getPrice());
        stmt.setDate(4, Date.valueOf(book.getPublicationDate()));
        stmt.setString(5, book.getInfo());
        stmt.setString(6, book.getStatus().name());
    }

    // Обертка для выполнения в транзакции
    private <R> R executeInTransaction(TransactionFunction<R> operation) {
        return (R) executeInTransaction((TransactionOperation) conn -> {
            return operation.execute(conn);
        });
    }

    @FunctionalInterface
    private interface TransactionFunction<R> {
        void execute(Connection connection) throws SQLException;
    }
}
package org.example.bookstore_app.dao;

import org.example.annotation.Inject;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookStatus;

import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class BookDAO implements GenericDAO<Book, Integer> {
    @Inject
    DBConnect connect;
    private Connection getConnection() throws Exception {
        return connect.getConnection();
    }

    private static final String TABLE_NAME = "book";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_PUBLICATION_DATE = "publicationDate";
    private static final String COLUMN_INFORMATION = "information";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_NAME;
    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME + " (" + COLUMN_NAME + ", " + COLUMN_AUTHOR + ", " + COLUMN_STATUS + ", " +
                    COLUMN_PRICE + ", " + COLUMN_PUBLICATION_DATE + ", " + COLUMN_INFORMATION +  ") VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE " + TABLE_NAME + " SET " + COLUMN_NAME + " = ?, " + COLUMN_AUTHOR + " = ?, " + COLUMN_STATUS  + " = ?, " +
                    COLUMN_PRICE + " = ?, " + COLUMN_PUBLICATION_DATE + " = ?, " + COLUMN_INFORMATION + " = ? WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

    @Override
    public Book findById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

        } catch (Exception e) {
            throw new RuntimeException("Error finding book with id: " + id, e);
        }
        return null;
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

        } catch (Exception e) {
            throw new RuntimeException("Error finding all books", e);
        }
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insertBook(book);
        } else {
            update(book);
            return book;
        }
    }

    private Book insertBook(Book book) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT,
                     Statement.RETURN_GENERATED_KEYS)) {

            setBookParameters(stmt, book);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1));
                }
            }
            return book;
        } catch (Exception e) {
            throw new RuntimeException("Error inserting book: " + book.getName(), e);
        }
    }

    @Override
    public void update(Book book) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            setBookParameters(stmt, book);
            stmt.setInt(7, book.getId());
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error updating book: " + book.getId(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error deleting book: " + id, e);
        }
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        try {
            Book book = new Book(
                    rs.getInt(COLUMN_ID),
                    rs.getString(COLUMN_NAME),
                    rs.getString(COLUMN_AUTHOR),
                    parsePrice(rs.getString(COLUMN_PRICE)), // Используем метод parsePrice
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
        } catch (ParseException e) {
            throw new SQLException("Error parsing price from database", e);
        }
    }

    private double parsePrice(String priceString) throws ParseException {
        if (priceString == null || priceString.trim().isEmpty()) {
            return 0.0;
        }

        // Убираем символы валюты и пробелы
        String cleaned = priceString.replaceAll("[^\\d,.-]", "").trim();

        // Если строка уже содержит точку как разделитель
        if (cleaned.contains(".") && !cleaned.contains(",")) {
            return Double.parseDouble(cleaned);
        }

        // Если содержит запятую, заменяем ее на точку
        if (cleaned.contains(",")) {
            cleaned = cleaned.replace(",", ".");
        }

        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            // Пробуем с локалью
            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE); // Используем локаль с запятой
            Number number = format.parse(priceString);
            return number.doubleValue();
        }
    }

    private void setBookParameters(PreparedStatement stmt, Book book) throws SQLException {
        stmt.setString(1, book.getName());
        stmt.setString(2, book.getAuthor());
        stmt.setString(3, book.getStatus().name());
        stmt.setDouble(4, book.getPrice());
        stmt.setDate(5, Date.valueOf(book.getPublicationDate()));
        stmt.setString(6, book.getInfo());
    }
}
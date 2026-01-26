package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class BookDAO implements GenericDAO<Book, Integer> {
    private final DBConnect connect;

    @Inject
    public BookDAO(DBConnect connect) {
        this.connect = connect;
        System.out.println("BookDAO created with connect = " + connect);
    }

    private Connection getConnection() throws Exception {
        if (connect == null) {
            System.out.println("DBConnect is not injected!");
        }

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
        try {
            syncSequence();
        } catch (Exception e) {
            System.out.println("Warning: Could not sync sequence: " + e.getMessage());
        }
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
            return null;
        } catch (Exception e) {
            System.out.println("Error finding book with id: " + id+" "+ e);
            return null;
        }
    }

    @Override
    public List<Book> findAll() {
        try {
            syncSequence();
        } catch (Exception e) {
            System.out.println("Warning: Could not sync sequence: " + e.getMessage());
        }
        List<Book> books = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
            return books;

        } catch (Exception e) {
            System.out.println("Error finding all books  "+ e);
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
        try {
            syncSequence();
        } catch (Exception e) {
            System.out.println("Warning: Could not sync sequence: " + e.getMessage());
        }
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
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
        try {
            syncSequence();
        } catch (Exception e) {
            System.out.println("Warning: Could not sync sequence: " + e.getMessage());
        }
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
        try {
            syncSequence();
        } catch (Exception e) {
            System.out.println("Warning: Could not sync sequence: " + e.getMessage());
        }
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
        } catch (Exception e) {
            throw new SQLException("Error parsing price from database", e);
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

    public void syncSequence() {
        String sql = "SELECT setval(pg_get_serial_sequence('book', 'id'), COALESCE((SELECT MAX(id) FROM book), 0) + 1, false)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("Error syncing sequence: " + e.getMessage());
        }
    }
}
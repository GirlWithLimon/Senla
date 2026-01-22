package org.example.bookstore_app.dao;

import org.example.annotation.Inject;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookCopyDAO implements GenericDAO<BookCopy, Integer> {
    @Inject
    DBConnect connect;
    private Connection getConnection() throws Exception {
        return connect.getConnection();
    }


    private static final String TABLE_NAME = "bookCopy";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ID_BOOK = "idBook";
    private static final String COLUMN_ID_STOCK = "idStock";
    private static final String COLUMN_ARRIVAL_DATE = "arrivalDate";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ARRIVAL_DATE + " DESC";
    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME + " (" + COLUMN_ID_BOOK + ", " +
                    COLUMN_ID_STOCK + ", " + COLUMN_ARRIVAL_DATE + ") VALUES (?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE " + TABLE_NAME + " SET " + COLUMN_ID_BOOK + " = ?, " +
                    COLUMN_ARRIVAL_DATE + " = ? WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_FIND_BY_BOOK_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID_BOOK + " = ?";

    private BookDAO bookDAO = new BookDAO();

    @Override
    public BookCopy findById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBookCopy(rs);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error finding book copy with id: " + id, e);
        }
    }

    @Override
    public List<BookCopy> findAll() {
        List<BookCopy> copies = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                copies.add(mapResultSetToBookCopy(rs));
            }
            return copies;

        } catch (Exception e) {
            throw new RuntimeException("Error finding all book copies", e);
        }
    }

    @Override
    public BookCopy save(BookCopy bookCopy) {
        if (bookCopy.getId() == 0) {
            return insertBookCopy(bookCopy);
        } else {
            update(bookCopy);
            return bookCopy;
        }
    }

    private BookCopy insertBookCopy(BookCopy bookCopy) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT,
                     Statement.RETURN_GENERATED_KEYS)) {

            setBookCopyParameters(stmt, bookCopy);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setIdUsingReflection(bookCopy, generatedKeys.getInt(1));
                }
            }
            return bookCopy;

        } catch (Exception e) {
            throw new RuntimeException("Error inserting book copy", e);
        }
    }

    @Override
    public void update(BookCopy bookCopy) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setInt(1, bookCopy.getIdBook());
            stmt.setDate(2, Date.valueOf(bookCopy.getArrivalDate()));
            stmt.setInt(3, bookCopy.getId());

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error updating book copy: " + bookCopy.getId(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error deleting book copy: " + id, e);
        }
    }

    private BookCopy mapResultSetToBookCopy(ResultSet rs) throws SQLException {
        int bookId = rs.getInt(COLUMN_ID_BOOK);
        try {
            Book book = bookDAO.findById(bookId);
            BookCopy copy = new BookCopy(
                    rs.getInt(COLUMN_ID),
                    book.getId(),
                    rs.getDate(COLUMN_ARRIVAL_DATE).toLocalDate()
            );
            return copy;
        } catch(SQLException e){
            throw new RuntimeException("Book not found with id: " + bookId, e);
        }
    }

    private void setBookCopyParameters(PreparedStatement stmt, BookCopy bookCopy) throws SQLException {
        stmt.setInt(1, bookCopy.getIdBook());
        stmt.setDate(2, Date.valueOf(bookCopy.getArrivalDate()));
    }

    private void setIdUsingReflection(BookCopy bookCopy, int id) {
        try {
            java.lang.reflect.Field idField = BookCopy.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(bookCopy, id);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set id for BookCopy", e);
        }
    }

    public List<BookCopy> findByBookId(Integer bookId) {
        List<BookCopy> copies = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_BOOK_ID)) {

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                copies.add(mapResultSetToBookCopy(rs));
            }
            return copies;

        } catch (Exception e) {
            throw new RuntimeException("Error finding book copies for book id: " + bookId, e);
        }
    }

    public int countByBookId(Integer bookId) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME +
                " WHERE " + COLUMN_ID_BOOK + " = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (Exception e) {
            throw new RuntimeException("Error counting book copies for book id: " + bookId, e);
        }
    }
}
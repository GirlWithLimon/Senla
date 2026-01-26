package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.BookCopy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookCopyDAO implements GenericDAO<BookCopy, Integer> {
    private final DBConnect connect;

    @Inject
    public BookCopyDAO(DBConnect connect) {
        this.connect = connect;
        System.out.println("BookDAO created with connect = " + connect);
    }

    private Connection getConnection() throws Exception {
        if (connect == null) {
            throw new IllegalStateException("DBConnect is not injected!");
        }
        return connect.getConnection();
    }


    private static final String TABLE_NAME = "bookCopy";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ID_BOOK = "idBook";
    private static final String COLUMN_ARRIVAL_DATE = "arrivalDate";
    private static final String COLUMN_SALE = "sale";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ARRIVAL_DATE + " DESC";
    private static final String SQL_SELECT_BY_BOOK_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID_BOOK + " = ?";
    private static final String SQL_SELECT_COUNT =
            "SELECT COUNT("+COLUMN_ID_BOOK+") FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID_BOOK + "GROUP BY"+COLUMN_ID_BOOK+" WHERE " + COLUMN_ID_BOOK + " = ?";

    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME + " (" + COLUMN_ID_BOOK + ", " + COLUMN_ARRIVAL_DATE + ","+ COLUMN_SALE +") VALUES (?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE " + TABLE_NAME + " SET " + COLUMN_ID_BOOK + " = ?, " +
                    COLUMN_ARRIVAL_DATE+ " = ?, " + COLUMN_SALE + " = ? WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";



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
    public List<BookCopy> findByBookId(Integer idBook) {
        List<BookCopy> copies = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_BOOK_ID)) {
            stmt.setInt(1, idBook);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                copies.add(mapResultSetToBookCopy(rs));
            }
            return copies;

        } catch (Exception e) {
            throw new RuntimeException("Error finding book copies for book id: " + idBook, e);
        }
    }
    public int findCountByIdBook(Integer idBook) {
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_COUNT)) {
            stmt.setInt(1, idBook);
            ResultSet rs = stmt.executeQuery();
            return  rs.getInt(1);

        } catch (Exception e) {
            throw new RuntimeException("Error finding book copies for book id: " + idBook, e);
        }
    }

    @Override
    public BookCopy save(BookCopy bookCopy) {
        if (bookCopy.getId() == 0) {
            return insertBookCopy(bookCopy);
        } else if (findById(bookCopy.getId())==null) {
            return insertBookCopy(bookCopy);
        }else {
            update(bookCopy);
            return bookCopy;
        }
    }

    private BookCopy insertBookCopy(BookCopy bookCopy) {
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            setBookCopyParameters(stmt, bookCopy);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bookCopy.setId(generatedKeys.getInt(1));
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
            stmt.setBoolean(3, bookCopy.getSale());
            stmt.setInt(4, bookCopy.getId());

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
            BookCopy copy = new BookCopy(
                    rs.getInt(COLUMN_ID),
                    rs.getInt(COLUMN_ID_BOOK),
                    rs.getDate(COLUMN_ARRIVAL_DATE).toLocalDate()
            );
            String saleStr = rs.getString(COLUMN_SALE);
            copy.setSale(saleStr.equals("true"));
            return copy;
        } catch(SQLException e){
            throw new RuntimeException("Book not found with id: " + bookId, e);
        }
    }

    private void setBookCopyParameters(PreparedStatement stmt, BookCopy bookCopy) throws SQLException {
        stmt.setInt(1, bookCopy.getIdBook());
        stmt.setDate(2, Date.valueOf(bookCopy.getArrivalDate()));
        stmt.setBoolean(3,bookCopy.getSale());
    }

}
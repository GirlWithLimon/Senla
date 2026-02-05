package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.BookCopy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookCopyDAO implements GenericDAO<BookCopy, Integer> {
    //для работы бд с экземплярами книг
    private static final Logger logger = LoggerFactory.getLogger(BookCopyDAO.class);
    private final DBConnect connect;

    @Inject
    public BookCopyDAO(DBConnect connect) {
        this.connect = connect;
        logger.debug("BookDAO created with connect = {}", connect);
    }

    private Connection getConnection() throws Exception {
        //проверка соединения
        if (connect == null) {
            logger.error("DBConnect не инициализирован!");
        }
        assert connect != null;
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
            "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_ID_BOOK + " = ?";

    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME + " (" + COLUMN_ID_BOOK + ", " + COLUMN_ARRIVAL_DATE + ","+ COLUMN_SALE +") VALUES (?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE " + TABLE_NAME + " SET " + COLUMN_ID_BOOK + " = ?, " +
                    COLUMN_ARRIVAL_DATE+ " = ?, " + COLUMN_SALE + " = ? WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";



    @Override
    public BookCopy findById(Integer id) {
        try {
            syncSequence();
        } catch (Exception e) {
            logger.warn("Не получается синхронизировать данные: {}", e.getMessage());
        }
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBookCopy(rs);
            }
            return null;
        } catch (Exception e) {
            logger.error("Ошибка при поиске копии с id: {}", id, e);
        }
        return null;
    }

    @Override
    public List<BookCopy> findAll() {
        try {
            syncSequence();
        } catch (Exception e) {
            logger.warn("Не получилось синхронизировать данные: {}", e.getMessage());
        }
        List<BookCopy> copies = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                copies.add(mapResultSetToBookCopy(rs));
            }
            return copies;

        } catch (Exception e) {
            logger.error("Ошибка при поиске всех экземпляров книг", e);
        }
        return null;
    }
    public List<BookCopy> findByBookId(Integer idBook) {
        try {
            syncSequence();
        } catch (Exception e) {
            logger.warn("Не удалось синхронизировать данные: {}", e.getMessage());
        }
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
            logger.error("Ошибка поиска экземпляра книги по id книги: {}", idBook, e);
        }
        return null;
    }
    public int findCountByIdBook(Integer idBook) {
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_COUNT)) {
            stmt.setInt(1, idBook);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (Exception e) {
            logger.error("Ошибка поиска количества экземпляров книг по id книги: {}", idBook, e);
        }
        return 0;
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
        try {
            syncSequence();
        } catch (Exception e) {
            logger.warn("Не удалось выполнить синхронизацию данных: {}", e.getMessage());
        }
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
            logger.error("Ошибки при добавлении экземпляра книги ", e);
        }
        return null;
    }

    @Override
    public void update(BookCopy bookCopy) {
        try {
            syncSequence();
        } catch (Exception e) {
            logger.warn("Не удалось выполнить синхронизацию данных: {}", e.getMessage());
        }
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setInt(1, bookCopy.getIdBook());
            stmt.setDate(2, Date.valueOf(bookCopy.getArrivalDate()));
            stmt.setBoolean(3, bookCopy.getSale());
            stmt.setInt(4, bookCopy.getId());

            stmt.executeUpdate();

        } catch (Exception e) {
            logger.error("Error updating book copy: " + bookCopy.getId(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        try {
            syncSequence();
        } catch (Exception e) {
            logger.warn("Не удалось выполнить синхронизацию данных: {}", e.getMessage());
        }
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            logger.error("Ошибка при удалении экземпляра: {}", id, e);
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
            logger.error("Книга с не найдена, id: {}", bookId, e);
        }
        return null;
    }

    private void setBookCopyParameters(PreparedStatement stmt, BookCopy bookCopy) throws SQLException {
        stmt.setInt(1, bookCopy.getIdBook());
        stmt.setDate(2, Date.valueOf(bookCopy.getArrivalDate()));
        stmt.setBoolean(3,bookCopy.getSale());
    }
    public void syncSequence() {
        String sql = "SELECT setval(pg_get_serial_sequence('bookCopy', 'id'), COALESCE((SELECT MAX(id) FROM bookCopy), 0) + 1, false)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            logger.warn("Ошибка синхронизации: {}", e.getMessage());
        }
    }
}

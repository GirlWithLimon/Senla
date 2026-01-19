package org.example.bookstore_app.dao;

import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.model.BookOrderItem;
import org.example.bookstore_app.model.OrderItemStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookOrderItemDAO implements GenericDAO<BookOrderItem, Integer> {
    private static final String TABLE_NAME = "orderItem";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ID_ORDERS = "idOrders";
    private static final String COLUMN_BOOK = "book";
    private static final String COLUMN_BOOK_COPY = "bookCopy";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_PRICE = "price";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME;
    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME + " (" + COLUMN_ID_ORDERS + ", " +
                    COLUMN_BOOK + ", " + COLUMN_BOOK_COPY + ", " + COLUMN_STATUS + ", " +
                    COLUMN_PRICE + ") VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE " + TABLE_NAME + " SET " + COLUMN_ID_ORDERS + " = ?, " +
                    COLUMN_BOOK + " = ?, " + COLUMN_BOOK_COPY + " = ?, " +
                    COLUMN_STATUS + " = ?, " + COLUMN_PRICE + " = ? WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_FIND_BY_ORDER_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID_ORDERS + " = ?";

    private BookDAO bookDAO = new BookDAO();
    private BookCopyDAO bookCopyDAO = new BookCopyDAO();

    @Override
    public Optional<BookOrderItem> findById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToOrderItem(rs));
            }
            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException("Error finding order item with id: " + id, e);
        }
    }

    @Override
    public List<BookOrderItem> findAll() {
        List<BookOrderItem> items = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                items.add(mapResultSetToOrderItem(rs));
            }
            return items;

        } catch (Exception e) {
            throw new RuntimeException("Error finding all order items", e);
        }
    }

    @Override
    public BookOrderItem save(BookOrderItem orderItem) {
        if (orderItem.getId() == 0) {
            return insertOrderItem(orderItem);
        } else {
            update(orderItem);
            return orderItem;
        }
    }

    private BookOrderItem insertOrderItem(BookOrderItem orderItem) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT,
                     Statement.RETURN_GENERATED_KEYS)) {

            setOrderItemParameters(stmt, orderItem);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setIdUsingReflection(orderItem, generatedKeys.getInt(1));
                }
            }
            return orderItem;

        } catch (Exception e) {
            throw new RuntimeException("Error inserting order item", e);
        }
    }

    @Override
    public void update(BookOrderItem orderItem) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            setOrderItemParameters(stmt, orderItem);
            stmt.setInt(6, orderItem.getId());

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error updating order item: " + orderItem.getId(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error deleting order item: " + id, e);
        }
    }

    private BookOrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        int bookId = rs.getInt(COLUMN_BOOK);
        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new SQLException("Book not found with id: " + bookId));

        BookOrderItem orderItem = new BookOrderItem(
                rs.getInt(COLUMN_ID),
                book
        );

        setOrderItemFieldsFromResultSet(orderItem, rs);

        return orderItem;
    }

    private void setOrderItemParameters(PreparedStatement stmt, BookOrderItem orderItem) throws SQLException {
        stmt.setInt(1, orderItem.getId()); // idOrders - пока используем id самого item
        stmt.setInt(2, orderItem.getBook().getId());

        if (orderItem.getBookCopy() != null) {
            stmt.setInt(3, orderItem.getBookCopy().getId());
        } else {
            stmt.setNull(3, Types.INTEGER);
        }

        stmt.setString(4, orderItem.getStatus().name());
        stmt.setDouble(5, orderItem.getPrice());
    }

    private void setIdUsingReflection(BookOrderItem orderItem, int id) {
        try {
            java.lang.reflect.Field idField = BookOrderItem.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(orderItem, id);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set id for BookOrderItem", e);
        }
    }

    private void setOrderItemFieldsFromResultSet(BookOrderItem orderItem, ResultSet rs) throws SQLException {
        try {
            int bookCopyId = rs.getInt(COLUMN_BOOK_COPY);
            if (!rs.wasNull() && bookCopyId > 0) {
                Optional<BookCopy> bookCopy = bookCopyDAO.findById(bookCopyId);
                bookCopy.ifPresent(orderItem::setBookCopy);
            }

            String statusStr = rs.getString(COLUMN_STATUS);
            OrderItemStatus status = OrderItemStatus.valueOf(statusStr);

            java.lang.reflect.Field statusField = BookOrderItem.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(orderItem, status);

            java.lang.reflect.Field priceField = BookOrderItem.class.getDeclaredField("price");
            priceField.setAccessible(true);
            priceField.set(orderItem, rs.getDouble(COLUMN_PRICE));

        } catch (Exception e) {
            throw new SQLException("Cannot set fields for BookOrderItem", e);
        }
    }

    public List<BookOrderItem> findByOrderId(Integer orderId) {
        List<BookOrderItem> items = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ORDER_ID)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(mapResultSetToOrderItem(rs));
            }
            return items;

        } catch (Exception e) {
            throw new RuntimeException("Error finding order items for order id: " + orderId, e);
        }
    }
}
package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookOrderItemDAO implements GenericDAO<BookOrderItem, Integer> {

    private final DBConnect connect;

    @Inject
    public BookOrderItemDAO(DBConnect connect) {
        this.connect = connect;
        System.out.println("BookDAO created with connect = " + connect);
    }
    private Connection getConnection() throws Exception {
        if (connect == null) {
            throw new IllegalStateException("DBConnect is not injected!");
        }
        return connect.getConnection();
    }

    private static final String TABLE_NAME = "orderItem";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_BOOK = "idBook";
    private static final String COLUMN_BOOK_COPY = "idBookCopy";
    private static final String COLUMN_ID_ORDERS = "idOrders";
    private static final String COLUMN_STATUS = "status";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME;
    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME + " (" + COLUMN_ID_ORDERS + ", " +
                    COLUMN_BOOK + ", " + COLUMN_BOOK_COPY + ", " + COLUMN_STATUS + ") VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE " + TABLE_NAME + " SET " + COLUMN_ID_ORDERS + " = ?, " +
                    COLUMN_BOOK + " = ?, " + COLUMN_BOOK_COPY + " = ?, " +
                    COLUMN_STATUS +  " = ? WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_FIND_BY_ORDER_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID_ORDERS + " = ?";



    @Override
    public BookOrderItem findById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToOrderItem(rs);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error finding order item with id: " + id, e);
        }
    }
    public List<BookOrderItem> findByOrderId(Integer idOrder) {
        List<BookOrderItem> items = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ORDER_ID)) {

            stmt.setInt(1, idOrder);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(mapResultSetToOrderItem(rs));
            }
            return items;

        } catch (Exception e) {
            throw new RuntimeException("Error finding order items for order id: " + idOrder, e);
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
        } else if (findById(orderItem.getId())==null) {
            return insertOrderItem(orderItem);
        } else {
            update(orderItem);
            return orderItem;
        }
    }

    private BookOrderItem insertOrderItem(BookOrderItem orderItem) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            setOrderItemParameters(stmt, orderItem);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderItem.setId(generatedKeys.getInt(1));
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
    public void deleteById(Integer id) {
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
        try {
           BookOrderItem orderItem = new BookOrderItem(
                    rs.getInt(COLUMN_ID),
                    rs.getInt(COLUMN_BOOK),
                   rs.getInt(COLUMN_ID_ORDERS)
            );
            String statusStr = rs.getString(COLUMN_STATUS);
            if (OrderItemStatus.NEW.name().equals(statusStr)) {
                orderItem.setStatus(OrderItemStatus.NEW);
            } else  if(OrderItemStatus.PENDING.name().equals(statusStr)){
                orderItem.setStatus(OrderItemStatus.PENDING);
            }else  if(OrderItemStatus.COMPLETED.name().equals(statusStr)){
                orderItem.setStatus(OrderItemStatus.COMPLETED);
            }else  if(OrderItemStatus.CANCELLED.name().equals(statusStr)){
                orderItem.setStatus(OrderItemStatus.CANCELLED);
            }
            orderItem.setIdBookCopy(rs.getInt(COLUMN_BOOK_COPY));
            return orderItem;

        } catch (Exception e) {
            throw new SQLException("Book not found with id: " + bookId);
        }
    }

    private void setOrderItemParameters(PreparedStatement stmt, BookOrderItem orderItem) throws SQLException {
        stmt.setInt(1, orderItem.getIdOrder());
        stmt.setInt(2, orderItem.getIdBook());
        if (orderItem.getIdBookCopy() != 0) {
            stmt.setInt(3, orderItem.getIdBookCopy());
        } else {
            stmt.setNull(3, Types.INTEGER);
        }
        stmt.setString(4, orderItem.getStatus().name());
    }



}
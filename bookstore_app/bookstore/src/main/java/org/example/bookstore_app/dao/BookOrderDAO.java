package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.BookOrder;
import org.example.bookstore_app.model.BookStatus;
import org.example.bookstore_app.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BookOrderDAO implements GenericDAO<BookOrder, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(BookOrderDAO.class);
    private final DBConnect connect;

    @Inject
    public BookOrderDAO(DBConnect connect) {
        this.connect = connect;
        logger.debug("BookDAO created with connect = {}", connect);
    }

    private Connection getConnection() throws Exception {
        if (connect == null) {
            throw new IllegalStateException("DBConnect is not injected!");
        }
        return connect.getConnection();
    }

    private static final String TABLE_NAME = "orders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_ORDER_DATE = "orderDate";
    private static final String COLUMN_CUSTOMER_NAME = "customerName";
    private static final String COLUMN_CUSTOMER_CONTACT = "customerContact";
    private static final String COLUMN_TOTAL_PRICE = "totalPrice";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ORDER_DATE + " DESC";
    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME + " (" + COLUMN_CUSTOMER_NAME + ", " +
                    COLUMN_CUSTOMER_CONTACT + ", " + COLUMN_ORDER_DATE + ", " +
                    COLUMN_STATUS + ", " + COLUMN_TOTAL_PRICE + ") VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE " + TABLE_NAME + " SET " + COLUMN_CUSTOMER_NAME + " = ?, " +
                    COLUMN_CUSTOMER_CONTACT + " = ?, " + COLUMN_STATUS + " = ?, " +
                    COLUMN_TOTAL_PRICE + " = ? WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

    @Override
    public BookOrder findById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
            return null;

        } catch (Exception e) {
            throw new RuntimeException("Error finding order with id: " + id, e);
        }
    }

    @Override
    public List<BookOrder> findAll() {
        List<BookOrder> orders = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
            return orders;

        } catch (Exception e) {
            throw new RuntimeException("Error finding all orders", e);
        }
    }

    @Override
    public BookOrder save(BookOrder order) {
        if (order.getId() == 0) {
            return insertOrder(order);
        } else {
            update(order);
            return order;
        }
    }

    private BookOrder insertOrder(BookOrder order) {
        try {
            syncSequence();
        } catch (Exception e) {
            System.out.println("Warning: Could not sync sequence: " + e.getMessage());
        }
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            setOrderParameters(stmt, order);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getInt(1));
                }
            }
            return order;

        } catch (Exception e) {
            throw new RuntimeException("Error inserting order", e);
        }
    }

    @Override
    public void update(BookOrder order) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            setOrderParametersForUpdate(stmt, order);
            stmt.setInt(5, order.getId());

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error updating order: " + order.getId(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error deleting order: " + id, e);
        }
    }

    private BookOrder mapResultSetToOrder(ResultSet rs) throws SQLException {
        BookOrder order = new BookOrder(
                rs.getInt(COLUMN_ID),
                rs.getString(COLUMN_CUSTOMER_NAME),
                rs.getString(COLUMN_CUSTOMER_CONTACT)
        );
        String statusStr = rs.getString(COLUMN_STATUS);
        if (OrderStatus.NEW.name().equals(statusStr)) {
            order.setStatus(OrderStatus.NEW);
        } else  if(OrderStatus.IN_PROCESS.name().equals(statusStr)){
            order.setStatus(OrderStatus.IN_PROCESS);
        }else  if(OrderStatus.PARTIALLY_COMPLETED.name().equals(statusStr)){
            order.setStatus(OrderStatus.PARTIALLY_COMPLETED);
        }else  if(OrderStatus.COMPLETED.name().equals(statusStr)){
            order.setStatus(OrderStatus.COMPLETED);
        }else  if(OrderStatus.CANCELLED.name().equals(statusStr)){
            order.setStatus(OrderStatus.CANCELLED);
        }
        order.setOrderDate(rs.getDate(COLUMN_ORDER_DATE).toLocalDate());
        order.setTotalPrice(rs.getDouble(COLUMN_TOTAL_PRICE));
        return order;
    }

    private void setOrderParameters(PreparedStatement stmt, BookOrder order) throws SQLException {
        stmt.setString(1, order.getCustomerName());
        stmt.setString(2, order.getCustomerContact());
        stmt.setDate(3, Date.valueOf(order.getOrderDate()));
        stmt.setString(4, order.getStatus().name());
        stmt.setDouble(5, order.getTotalPrice());
    }

    private void setOrderParametersForUpdate(PreparedStatement stmt, BookOrder order) throws SQLException {
        stmt.setString(1, order.getCustomerName());
        stmt.setString(2, order.getCustomerContact());
        stmt.setString(3, order.getStatus().name());
        stmt.setDouble(4, order.getTotalPrice());
    }
    public void syncSequence() {
        String sql = "SELECT setval(pg_get_serial_sequence('orders', 'id'), COALESCE((SELECT MAX(id) FROM orders), 0) + 1, false)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("Error syncing sequence: " + e.getMessage());
        }
    }

}
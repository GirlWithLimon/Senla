package org.example.bookstore_app.dao;

import org.example.bookstore_app.model.BookOrder;
import org.example.bookstore_app.model.OrderStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookOrderDAO implements GenericDAO<BookOrder, Integer> {
    private static final String TABLE_NAME = "orders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CUSTOMER_NAME = "customerName";
    private static final String COLUMN_CUSTOMER_CONTACT = "customerContact";
    private static final String COLUMN_ORDER_DATE = "orderDate";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_TOTAL_PRICE = "totalPrice";
    private static final String COLUMN_ID_STOCK = "idStock"; // Внешний ключ

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ORDER_DATE + " DESC";
    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME + " (" + COLUMN_CUSTOMER_NAME + ", " +
                    COLUMN_CUSTOMER_CONTACT + ", " + COLUMN_ORDER_DATE + ", " +
                    COLUMN_STATUS + ", " + COLUMN_TOTAL_PRICE + ", " + COLUMN_ID_STOCK +
                    ") VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE " + TABLE_NAME + " SET " + COLUMN_CUSTOMER_NAME + " = ?, " +
                    COLUMN_CUSTOMER_CONTACT + " = ?, " + COLUMN_STATUS + " = ?, " +
                    COLUMN_TOTAL_PRICE + " = ? WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

    @Override
    public Optional<BookOrder> findById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToOrder(rs));
            }
            return Optional.empty();

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
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT,
                     Statement.RETURN_GENERATED_KEYS)) {

            setOrderParameters(stmt, order);
            stmt.setInt(6, 1); // idStock - пока фиксированное значение

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Для BookOrder id - final поле, нужен рефлекшн
                    setIdUsingReflection(order, generatedKeys.getInt(1));
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

        setOrderFieldsFromResultSet(order, rs);

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

    private void setIdUsingReflection(BookOrder order, int id) {
        try {
            java.lang.reflect.Field idField = BookOrder.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(order, id);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set id for BookOrder", e);
        }
    }

    private void setOrderFieldsFromResultSet(BookOrder order, ResultSet rs) throws SQLException {
        try {
            String statusStr = rs.getString(COLUMN_STATUS);
            OrderStatus status = OrderStatus.valueOf(statusStr);

            java.lang.reflect.Field statusField = BookOrder.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(order, status);

            java.lang.reflect.Field dateField = BookOrder.class.getDeclaredField("orderDate");
            dateField.setAccessible(true);
            dateField.set(order, rs.getDate(COLUMN_ORDER_DATE).toLocalDate());

            java.lang.reflect.Field priceField = BookOrder.class.getDeclaredField("totalPrice");
            priceField.setAccessible(true);
            priceField.set(order, rs.getDouble(COLUMN_TOTAL_PRICE));

        } catch (Exception e) {
            throw new SQLException("Cannot set fields for BookOrder", e);
        }
    }

    // Метод для поиска заказов по статусу
    public List<BookOrder> findByStatus(OrderStatus status) {
        List<BookOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
            return orders;

        } catch (Exception e) {
            throw new RuntimeException("Error finding orders by status: " + status, e);
        }
    }
}
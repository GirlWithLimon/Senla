package org.example.bookstore_app.dao;

import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookOrderItem;
import org.example.bookstore_app.model.Request;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestDAO implements GenericDAO<Request, Integer> {
    private static final String TABLE_NAME = "request";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ID_ORDER_ITEM = "idOrderItem";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME;
    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME + " (" + COLUMN_ID_ORDER_ITEM + ") VALUES (?)";
    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_FIND_BY_BOOK_ID =
            "SELECT r.* FROM " + TABLE_NAME + " r " +
                    "JOIN orderItem oi ON r." + COLUMN_ID_ORDER_ITEM + " = oi.id " +
                    "WHERE oi.book = ?";

    private BookOrderItemDAO orderItemDAO = new BookOrderItemDAO();

    @Override
    public Optional<Request> findById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToRequest(rs));
            }
            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException("Error finding request with id: " + id, e);
        }
    }

    @Override
    public List<Request> findAll() {
        List<Request> requests = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
            return requests;

        } catch (Exception e) {
            throw new RuntimeException("Error finding all requests", e);
        }
    }

    @Override
    public Request save(Request request) {
        if (request.getId() == 0) {
            return insertRequest(request);
        } else {
            throw new UnsupportedOperationException("Request cannot be updated, only created or deleted");
        }
    }

    private Request insertRequest(Request request) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, request.getOrderItem().getId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setIdUsingReflection(request, generatedKeys.getInt(1));
                }
            }
            return request;

        } catch (Exception e) {
            throw new RuntimeException("Error inserting request", e);
        }
    }

    @Override
    public void update(Request entity) {
        throw new UnsupportedOperationException("Request cannot be updated, only created or deleted");
    }

    @Override
    public void delete(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error deleting request: " + id, e);
        }
    }

    private Request mapResultSetToRequest(ResultSet rs) throws SQLException {
        int orderItemId = rs.getInt(COLUMN_ID_ORDER_ITEM);
        BookOrderItem orderItem = orderItemDAO.findById(orderItemId)
                .orElseThrow(() -> new SQLException("OrderItem not found with id: " + orderItemId));

        Request request = new Request(
                rs.getInt(COLUMN_ID),
                orderItem
        );

        return request;
    }

    private void setIdUsingReflection(Request request, int id) {
        try {
            java.lang.reflect.Field idField = Request.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(request, id);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set id for Request", e);
        }
    }

    public List<Request> findByBookId(Integer bookId) {
        List<Request> requests = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_BOOK_ID)) {

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
            return requests;

        } catch (Exception e) {
            throw new RuntimeException("Error finding requests for book id: " + bookId, e);
        }
    }

    public int countByBookId(Integer bookId) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " r " +
                "JOIN orderItem oi ON r." + COLUMN_ID_ORDER_ITEM + " = oi.id " +
                "WHERE oi.book = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (Exception e) {
            throw new RuntimeException("Error counting requests for book id: " + bookId, e);
        }
    }
}
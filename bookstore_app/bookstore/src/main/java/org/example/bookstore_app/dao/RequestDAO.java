package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.BookOrderItem;
import org.example.bookstore_app.model.Request;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class RequestDAO implements GenericDAO<Request, Integer> {
    private final DBConnect connect;

    @Inject
    public RequestDAO(DBConnect connect) {
        this.connect = connect;
        System.out.println("BookDAO created with connect = " + connect);
    }

    private Connection getConnection() throws Exception {
        if (connect == null) {
            throw new IllegalStateException("DBConnect is not injected!");
        }
        return connect.getConnection();
    }

    private static final String TABLE_NAME = "request";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ID_ORDER_ITEM = "idOrderItem";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME;
    private static final String SQL_SELECT_BY_BOOK_ID =
            "SELECT r.* FROM " + TABLE_NAME + " r " +
                    "JOIN orderItem oi ON r." + COLUMN_ID_ORDER_ITEM + " = oi.id " +
                    "WHERE oi.book = ?";
    private static final String SQL_SELECT_BY_ORDER_ITEM_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID_ORDER_ITEM + " = ?";
    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME + " (" + COLUMN_ID_ORDER_ITEM + ") VALUES (?)";
    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";




    @Override
    public Request findById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRequest(rs);
            }
            return null;

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
    public List<Request> findByBookId(Integer bookId) {
        List<Request> requests = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_BOOK_ID)) {

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
    public Request findByIdOrderItem(int idOrderItem) {

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ORDER_ITEM_ID)) {

            stmt.setInt(1, idOrderItem);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRequest(rs);
            }
            return null;

        } catch (Exception e) {
            throw new RuntimeException("Error finding requests for orderItem id: " + idOrderItem, e);
        }
    }


    @Override
    public Request save(Request request) {
        if (request.getId() == 0) {
            return insertRequest(request);
        } else if (findById(request.getId())==null) {
            return insertRequest(request);
        }else {
            throw new UnsupportedOperationException("Request cannot be updated, only created or deleted");
        }
    }

    private Request insertRequest(Request request) {
        try {
            syncSequence();
        } catch (Exception e) {
            System.out.println("Warning: Could not sync sequence: " + e.getMessage());
        }
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, request.getIdOrderItem());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    request.setId(generatedKeys.getInt(1));
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
    public void deleteById(Integer id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error deleting request: " + id, e);
        }
    }

    private Request mapResultSetToRequest(ResultSet rs) throws SQLException {
        try{
            Request request = new Request(
                    rs.getInt(COLUMN_ID),
                    rs.getInt(COLUMN_ID_ORDER_ITEM)
            );

            return request;
        }catch (Exception e){
            throw new SQLException("OrderItem not found with rs: " + rs);
        }
    }


//может понадобиться, а может и нет
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
    public void syncSequence() {
        String sql = "SELECT setval(pg_get_serial_sequence('request', 'id'), COALESCE((SELECT MAX(id) FROM request), 0) + 1, false)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("Error syncing sequence: " + e.getMessage());
        }
    }
}
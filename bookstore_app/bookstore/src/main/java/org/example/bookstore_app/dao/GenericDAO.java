package org.example.bookstore_app.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GenericDAO<T, ID> {

    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T entity);
    void update(T entity);
    void delete(ID id);

    default Connection getConnection() throws Exception {
        return DBConnect.getInstance().getConnection();
    }

    default <R> R executeInTransaction(TransactionOperation<R> operation) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            R result = operation.execute(conn);

            conn.commit();
            return result;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Transaction failed", e);

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    @FunctionalInterface
    interface TransactionOperation<R> {
        R execute(Connection connection) throws SQLException;
    }
}
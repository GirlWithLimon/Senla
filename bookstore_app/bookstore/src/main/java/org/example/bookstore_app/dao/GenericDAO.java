package org.example.bookstore_app.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GenericDAO<T, ID> {
    // CRUD операции
    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T entity);
    void update(T entity);
    void delete(ID id);

    // Получение соединения (для транзакций)
    default Connection getConnection() throws Exception {
        return DBConnect.getInstance().getConnection();
    }

    // Метод для работы с транзакциями
    default void executeInTransaction(TransactionOperation operation) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            operation.execute(conn);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {

                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {

                }
            }
        }
    }

    @FunctionalInterface
    interface TransactionOperation {
        void execute(Connection connection) throws SQLException;
    }
}

package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.BookOrder;
import org.example.bookstore_app.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@Component
public class BookOrderDAO extends HibernateAbstractDao<BookOrder, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(BookOrderDAO.class);

    @Inject
    public BookOrderDAO() {
        super(BookOrder.class);
        logger.debug("BookDAO created with connect}");
    }

}

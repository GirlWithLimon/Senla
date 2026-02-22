package org.example.bookstore_app.dao;

import org.example.bookstore_app.model.BookOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class BookOrderDAO extends HibernateAbstractDao<BookOrder, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(BookOrderDAO.class);

    public BookOrderDAO() {
        super(BookOrder.class);
        logger.debug("BookOrderDAO created");
    }
}
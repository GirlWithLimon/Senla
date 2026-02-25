package org.example.bookstore_app.dao;

import org.example.bookstore_app.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class BookDAO extends HibernateAbstractDao<Book, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(BookDAO.class);

    public BookDAO() {
        super(Book.class);
        logger.debug("BookDAO created");
    }
}
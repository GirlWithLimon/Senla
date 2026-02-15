package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;


@Component
public class BookDAO extends HibernateAbstractDao<Book, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(BookDAO.class);

    @Inject
    public BookDAO() {
        super(Book.class);
        logger.debug("BookDAO created with connect");
    }


}

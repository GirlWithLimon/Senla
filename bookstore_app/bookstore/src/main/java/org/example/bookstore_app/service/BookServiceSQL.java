package org.example.bookstore_app.service;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.dao.BookDAO;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.service.GenericServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class BookServiceSQL extends GenericServiceImpl<Book, Integer, BookDAO> {
    private static final Logger logger = LoggerFactory.getLogger(BookServiceSQL.class);

    @Inject
    public BookServiceSQL(BookDAO bookDAO) {
        super(bookDAO);
    }

    @Override
    public List<Book> findAll() {
        logger.debug("Поиск всех книг");
        return  super.findAll();
    }
    @Override
    public Book find(Integer id) {
        logger.debug("Поиск книги с id: {}",id);
        return  super.find(id);
    }
    @Override
    public void delete(Integer id) {
        logger.debug("Удаление книги с id: {}",id);
        super.delete(id);
    }
}

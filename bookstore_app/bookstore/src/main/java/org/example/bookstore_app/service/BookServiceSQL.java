package org.example.bookstore_app.service;


import org.example.bookstore_app.dao.BookDAO;
import org.example.bookstore_app.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("bookServiceSQL")
public class BookServiceSQL extends GenericServiceImpl<Book, Integer, BookDAO>
implements IBookService{
    private static final Logger logger = LoggerFactory.getLogger(BookServiceSQL.class);

    @Autowired

    public BookServiceSQL(BookDAO bookDAO) {
        super(bookDAO);
    }

    @Override
    public Integer save(Book book) {
        logger.debug("Сохранение книги: name={}, id before save={}", book.getName(), book.getId());
        Integer id = super.save(book);
        logger.debug("Книга сохранена, id after save={}", id);
        return id;
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

package org.example.bookstore_app.service;

import org.springframework.beans.factory.annotation.Autowired;  // ← заменили
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;  // ← заменили
import org.example.bookstore_app.dao.BookCopyDAO;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
@Qualifier("bookCopyServiceSQL")
public class BookCopyServiceSQL extends GenericServiceImpl<BookCopy, Integer, BookCopyDAO>
implements IBookCopyService{
    private static final Logger logger = LoggerFactory.getLogger(BookCopyServiceSQL.class);

    @Autowired
    public BookCopyServiceSQL(BookCopyDAO bookCopyDAO) {
        super(bookCopyDAO);
    }

    @Override
    public List<BookCopy> findAll() {
        logger.debug("Поиск всех экземпляров книг");
        return super.findAll();
    }

    @Override
    public BookCopy find(Integer id) {
        logger.debug("Поиск экземпляра книги с id: {}", id);
        return super.find(id);
    }

    @Override
    public void delete(Integer id) {
        logger.debug("Удаление экземпляра книги с id: {}", id);
        super.delete(id);
    }

    public List<BookCopy> findWithBookId() {
        logger.debug("Поиск всех копий с книгами");
        return defaultRepository.findWithBookId();
    }

    public List<BookCopy> findByBookId(int idBook) {
        return defaultRepository.findByBookId(idBook);
    }

    public int findCountByIdBook(Integer idBook) {
        return defaultRepository.findCountByIdBook(idBook);
    }
}
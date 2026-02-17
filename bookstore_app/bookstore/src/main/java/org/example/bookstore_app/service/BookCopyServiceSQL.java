package org.example.bookstore_app.service;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.dao.BookCopyDAO;
import org.example.bookstore_app.dao.BookDAO;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class BookCopyServiceSQL extends GenericServiceImpl<BookCopy, Integer, BookCopyDAO> {
    private static final Logger logger = LoggerFactory.getLogger(BookCopyServiceSQL.class);

    @Inject
    public BookCopyServiceSQL(BookCopyDAO bookCopyDAO) {
        super(bookCopyDAO);
    }

    @Override
    public List<BookCopy> findAll() {
        logger.debug("Поиск всех экземпляров книг");
        return  super.findAll();
    }
    @Override
    public BookCopy find(Integer id) {
        logger.debug("Поиск экземпляра книги с id: {}",id);
        return  super.find(id);
    }
    @Override
    public void delete(Integer id) {
        logger.debug("Удаление экземпляра книги с id: {}",id);
        super.delete(id);
    }
    public List<BookCopy> findWithBookId() {
        logger.debug("Поиск всех копий с книгами");
        return defaultRepository.findWithBookId();
    }
    public List<BookCopy> findByBookId(int idBook) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            List<BookCopy> entitys = defaultRepository.findByBookId(idBook);
            transaction.commit();
            return entitys;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Ошибка при поиске всех экземпляров книги", e);
        } finally {
            HibernateUtil.closeSession();
        }
    }
    public int findCountByIdBook(Integer idBook){
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            int entitys = defaultRepository.findCountByIdBook(idBook);
            transaction.commit();
            return entitys;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Ошибка при поиске числа экземпляров книги", e);
        } finally {
            HibernateUtil.closeSession();
        }
    }
}

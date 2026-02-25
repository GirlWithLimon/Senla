package org.example.bookstore_app.dao;

import org.example.bookstore_app.model.BookCopy;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class BookCopyDAO extends HibernateAbstractDao<BookCopy, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(BookCopyDAO.class);

    public BookCopyDAO() {
        super(BookCopy.class);
        logger.debug("BookCopyDAO created");
    }

    public List<BookCopy> findByBookId(int bookId) {
        logger.debug("Поиск книг по bookId: {}", bookId);
        Session session = getCurrentSession();
        Query<BookCopy> query = session.createQuery(
                "FROM BookCopy bc WHERE bc.book.id = :bookId", BookCopy.class);
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }

    public List<BookCopy> findWithBookId() {
        logger.debug("Поиск всех копий с bookId");
        Session session = getCurrentSession();
        String hql = "SELECT bc FROM BookCopy bc JOIN FETCH bc.book";
        Query<BookCopy> query = session.createQuery(hql, BookCopy.class);
        return query.getResultList();
    }


    public int findCountByIdBook(Integer idBook) {
        logger.debug("Количество копий с bookId: {}", idBook);
        Session session = getCurrentSession();
        String hql = "SELECT COUNT(bc) FROM BookCopy bc WHERE bc.book.id = :bookId";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("bookId", idBook);
        return query.uniqueResult().intValue();
    }
}
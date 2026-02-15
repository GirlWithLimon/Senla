package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.util.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.hibernate.query.Query;
import java.util.List;

@Component
public class BookCopyDAO extends HibernateAbstractDao<BookCopy, Integer> {
    //для работы бд с экземплярами книг
    private static final Logger logger = LoggerFactory.getLogger(BookCopyDAO.class);

    @Inject
    public BookCopyDAO() {
        super(BookCopy.class);
        logger.debug("BookDAO created with connect");
    }

    public List<BookCopy> findByBookId(int bookId) {
        logger.debug("Поиск книг по bookId: {}", bookId);
        Session session = HibernateUtil.getCurrentSession();

        String hql = "FROM BookCopy bc WHERE bc.book.id = :bookId";
        Query<BookCopy> query = session.createQuery(hql, BookCopy.class);
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }
    public int findCountByIdBook(Integer idBook) {
        logger.debug("Количество копий с bookId: {}", idBook);
        Session session = HibernateUtil.getCurrentSession();

        String hql = "SELECT COUNT(bc) FROM BookCopy bc WHERE bc.book.id = :bookId";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("bookId", idBook);

        return query.uniqueResult().intValue();
    }

}

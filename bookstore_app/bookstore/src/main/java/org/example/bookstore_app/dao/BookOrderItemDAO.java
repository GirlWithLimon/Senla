package org.example.bookstore_app.dao;

import org.example.bookstore_app.model.BookOrderItem;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class BookOrderItemDAO extends HibernateAbstractDao<BookOrderItem, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(BookOrderItemDAO.class);

    public BookOrderItemDAO() {
        super(BookOrderItem.class);
        logger.debug("BookOrderItemDAO created");
    }

    @Override
    public BookOrderItem find(Integer id) {
        try {
            logger.debug("Поиск подзапроса с id: {}", id);
            return super.find(id);
        } catch (Exception e) {
            logger.debug("Ошибка при поиске подзапроса по id: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<BookOrderItem> findByOrderId(Integer idOrder) {
        logger.debug("Поиск подзапросов по idOrder: {}", idOrder);
        Session session = getCurrentSession();
        String hql = "FROM BookOrderItem oi WHERE oi.order.id = :idOrder";
        Query<BookOrderItem> query = session.createQuery(hql, BookOrderItem.class);
        query.setParameter("idOrder", idOrder);
        return query.getResultList();
    }

    public List<BookOrderItem> findByOrderIdWithBooks(Integer idOrder) {
        logger.debug("Поиск подзапросов с книгами по idOrder: {}", idOrder);
        Session session = getCurrentSession();
        String hql = "SELECT oi FROM BookOrderItem oi JOIN FETCH oi.book WHERE oi.order.id = :idOrder";
        Query<BookOrderItem> query = session.createQuery(hql, BookOrderItem.class);
        query.setParameter("idOrder", idOrder);
        return query.getResultList();
    }

    public double findSumByIdOrder(Integer idOrder) {
        logger.debug("Поиск суммы заказа по idOrder: {}", idOrder);
        Session session = getCurrentSession();
        String hql = "SELECT SUM(oi.book.price) FROM BookOrderItem oi JOIN oi.book WHERE oi.order.id = :idOrder";
        Query<Double> query = session.createQuery(hql, Double.class);
        query.setParameter("idOrder", idOrder);
        return query.getSingleResult();
    }

    public List<BookOrderItem> findByOrderIdWithAllData(Integer idOrder) {
        logger.debug("Поиск подзапросов со всеми данными по idOrder: {}", idOrder);
        Session session = getCurrentSession();
        String hql = "SELECT DISTINCT oi FROM BookOrderItem oi " +
                "LEFT JOIN FETCH oi.bookCopy " +
                "LEFT JOIN FETCH oi.book " +
                "LEFT JOIN FETCH oi.request " +
                "WHERE oi.order.id = :idOrder";
        Query<BookOrderItem> query = session.createQuery(hql, BookOrderItem.class);
        query.setParameter("idOrder", idOrder);
        return query.getResultList();
    }
}
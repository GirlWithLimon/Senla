package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.*;
import org.example.bookstore_app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

@Component
public class BookOrderItemDAO extends HibernateAbstractDao<BookOrderItem, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(BookOrderItemDAO.class);

    @Inject
    public BookOrderItemDAO() {
        super(BookOrderItem.class);
        logger.debug("BookDAO created with connect");
    }

    @Override
    public BookOrderItem find(Integer id) {
       try {
            logger.debug("Поиск подзапроса с id: {}", id);
            return super.find(id);
       } catch (Exception e){
           logger.debug("Ошибка при поиске подзапроса по id: {}",
                   String.valueOf(e));
           throw new RuntimeException(e);
       }
    }
    public List<BookOrderItem> findByOrderId(Integer idOrder) {
        logger.debug("Поиск подзапросов по idOrder: {}", idOrder);
        Session session = HibernateUtil.getCurrentSession();

        String hql = "FROM BookOrderItem oi WHERE oi.order.id = :idOrder";
        Query<BookOrderItem> query = session.createQuery(hql, BookOrderItem.class);
        query.setParameter("idOrder", idOrder);
        return query.getResultList();
    }

    public List<BookOrderItem> findByOrderIdWithBooks(Integer idOrder) {
        logger.debug("Поиск подзапросов с книгами по idOrder: {}", idOrder);
        Session session = HibernateUtil.getCurrentSession();

        String hql = "SELECT oi FROM BookOrderItem oi " +
                "JOIN FETCH oi.book " +
                "WHERE oi.order.id = :idOrder";
        Query<BookOrderItem> query = session.createQuery(hql, BookOrderItem.class);
        query.setParameter("idOrder", idOrder);
        return query.getResultList();
    }
    public double findSumByIdOrder(Integer idOrder) {
        logger.debug("Поиск суммы заказа по idOrder: {}", idOrder);
        Session session = HibernateUtil.getCurrentSession();

        String hql = "SELECT SUM(oi.book.price) FROM BookOrderItem oi " +
                "JOIN oi.book " +
                "WHERE oi.order.id = :idOrder";
        Query<Double> query = session.createQuery(hql, Double.class);
        query.setParameter("idOrder", idOrder);
        return query.getSingleResult();
    }
    public List<BookOrderItem> findByOrderIdWithAllData(Integer idOrder) {
        logger.debug("Поиск подзапросов со всеми данными по idOrder: {}", idOrder);
        Session session = HibernateUtil.getCurrentSession();

        String hql = "SELECT DISTINCT oi FROM BookOrderItem oi " +
                "LEFT JOIN FETCH oi.bookCopy " +  // ← загружаем bookCopy
                "LEFT JOIN FETCH oi.book " +      // ← загружаем book
                "LEFT JOIN FETCH oi.request " +    // ← загружаем request
                "WHERE oi.order.id = :idOrder";

        Query<BookOrderItem> query = session.createQuery(hql, BookOrderItem.class);
        query.setParameter("idOrder", idOrder);
        return query.getResultList();
    }
    @Override
    public List<BookOrderItem> findAll() {
        logger.debug("Вывод всех подзапросов");
        return super.findAll();
    }

    @Override
    public Integer save(BookOrderItem orderItem) {
        logger.debug("Сохранение подзапроса с id = {}",orderItem.getId());
        return super.save(orderItem);
    }

    @Override
    public void update(BookOrderItem orderItem) {
        logger.debug("Изменение подзапроса с id = {}",orderItem.getId());
        super.update(orderItem);
    }

    @Override
    public void delete(Integer id) {
        logger.debug("Удаление подзапроса с id = {}",id);
        super.delete(id);
    }

}

package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.BookOrderItem;
import org.example.bookstore_app.model.Request;
import org.example.bookstore_app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import java.sql.*;
import java.util.List;

@Component
public class RequestDAO extends HibernateAbstractDao<Request, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(RequestDAO.class);

    @Inject
    public RequestDAO() {
        super(Request.class);
        System.out.println("BookDAO created with connect" );
    }


    @Override
    public Request find(Integer id) {
        try{
            logger.debug("Поиск запроса с id = {}", id);
            return super.find(id);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка поиска запросов с id: " + id, e);
        }
    }

    @Override
    public List<Request> findAll() {
        try {
            logger.debug("Поиск всех запросов.");
            return super.findAll();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске всех запросов ", e);
        }
    }

    public Request findByIdOrderItem(int orderItemId) {
        logger.debug("Поиск запроса по orderItemId: {}", orderItemId);
        Session session = HibernateUtil.getCurrentSession();

        // ИСПРАВЛЕНО: используем правильный путь через связанный объект
        String hql = "FROM Request r WHERE r.orderItem.id = :orderItemId";
        Query<Request> query = session.createQuery(hql, Request.class);
        query.setParameter("orderItemId", orderItemId);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.debug("Запрос не найден для orderItemId: {}", orderItemId);
            return null;
        } catch (Exception e) {
            logger.error("Ошибка при поиске запроса по orderItemId: {}", orderItemId, e);
            throw new RuntimeException("Ошибка поиска запроса по orderItemId: " + orderItemId, e);
        }
    }
    public List<Request> findByRequestIdWithBook(Integer idBook) {
        logger.debug("Поиск заяввок со всеми данными по idBook: {}", idBook);
        Session session = HibernateUtil.getCurrentSession();

        String hql = "SELECT DISTINCT r FROM Request r "
                + "LEFT JOIN FETCH r.orderItem oi "  // ← загружаем OrderItem
                + "LEFT JOIN FETCH oi.book "  // ← загружаем book
                + "LEFT JOIN FETCH oi.bookCopy " // ← загружаем bookCopy
                + "LEFT JOIN FETCH oi.order "
                + "WHERE oi.book.id = :idBook ORDER BY oi.order.orderDate DESC";

        Query<Request> query = session.createQuery(hql, Request.class);
        query.setParameter("idBook", idBook);
        query.setMaxResults(1);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            logger.debug("Запросы не найдены для книги id: {}", idBook);
            return null;
        }
    }

    @Override
    public Integer save(Request request) {
        try {
            logger.debug("охранение запроса с id = {}",request.getId());
            return super.save(request);
        } catch (Exception e){
            logger.debug("Ошибка сохранения запроса с id ={}", request.getId());
            throw new UnsupportedOperationException(
                    "Ошибка сохранения запроса с id ="
                    +request.getId(), e
            );
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            logger.debug("Удаление запроса с id = {}", id);
            super.delete(id);
        } catch (Exception e) {
            logger.debug("Ошибка при удалении запроса с id = {}", id);
            throw new RuntimeException("Ошибка удаления запроса: " + id, e);
        }
    }

}

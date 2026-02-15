package org.example.bookstore_app.service;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.dao.BookCopyDAO;
import org.example.bookstore_app.dao.BookOrderItemDAO;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.model.BookOrderItem;
import org.example.bookstore_app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class OrderItemServiceSQL extends GenericServiceImpl<BookOrderItem, Integer, BookOrderItemDAO> {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemServiceSQL.class);

    @Inject
    public OrderItemServiceSQL(BookOrderItemDAO bookOrderItem) {
        super(bookOrderItem);
    }

    @Override
    public List<BookOrderItem> findAll() {
        logger.debug("Поиск всех экземпляров книг");
        return  super.findAll();
    }
    @Override
    public BookOrderItem find(Integer id) {
        logger.debug("Поиск экземпляра книги с id: {}",id);
        return  super.find(id);
    }
    @Override
    public void delete(Integer id) {
        logger.debug("Удаление экземпляра книги с id: {}",id);
        super.delete(id);
    }
    public List<BookOrderItem> findByOrderId(Integer idOrder){
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            List<BookOrderItem> entitys = defaultRepository.findByOrderId(idOrder);
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
    public List<BookOrderItem> findByOrderIdWithBooks(Integer idOrder){
        logger.debug("Поиск позиций заказа с книгами по idOrder: {}", idOrder);
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            List<BookOrderItem> items = defaultRepository.findByOrderIdWithBooks(idOrder);
            transaction.commit();
            return items;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Ошибка при поиске позиций заказа с книгами", e);
            throw new RuntimeException("Ошибка при поиске позиций заказа", e);
        } finally {
            HibernateUtil.closeSession();
        }
    }

}

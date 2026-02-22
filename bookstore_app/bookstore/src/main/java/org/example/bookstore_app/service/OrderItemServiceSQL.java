package org.example.bookstore_app.service;


import org.example.bookstore_app.dao.BookOrderItemDAO;
import org.example.bookstore_app.model.BookOrderItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("orderItemServiceSQL")
public class OrderItemServiceSQL extends GenericServiceImpl<BookOrderItem, Integer, BookOrderItemDAO>
implements IOrderItemService{
    private static final Logger logger = LoggerFactory.getLogger(OrderItemServiceSQL.class);

    @Autowired
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
        logger.debug("Поиск подзаказов по idOrder: {}", idOrder);
       return defaultRepository.findByOrderId(idOrder);
    }
    public List<BookOrderItem> findByOrderIdWithBooks(Integer idOrder){
        logger.debug("Поиск позиций заказа с книгами по idOrder: {}", idOrder);
        return defaultRepository.findByOrderIdWithBooks(idOrder);
    }
    public double findSumByIdOrder(Integer idOrder) {
        logger.debug("Поиск суммы заказа по idOrder: {}", idOrder);
        return defaultRepository.findSumByIdOrder(idOrder);
    }
    public List<BookOrderItem> findByOrderIdWithAllData(Integer idOrder) {
        logger.debug("Поиск позиций заказа со всеми данными по idOrder: {}", idOrder);
       return defaultRepository.findByOrderIdWithAllData(idOrder);
    }

}

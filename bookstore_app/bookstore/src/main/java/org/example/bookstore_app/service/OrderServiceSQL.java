package org.example.bookstore_app.service;

import org.example.bookstore_app.dao.BookOrderDAO;
import org.example.bookstore_app.model.BookOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("orderServiceSQL")
public class OrderServiceSQL extends GenericServiceImpl<BookOrder, Integer, BookOrderDAO>
implements IOrderService{
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceSQL.class);

    @Autowired
    public OrderServiceSQL(BookOrderDAO bookOrderDAO) {
        super(bookOrderDAO);
    }

    @Override
    public Integer save(BookOrder order) {
        logger.debug("Сохранение заказа");
        return  super.save(order);
    }
    @Override
    public List<BookOrder> findAll() {
        logger.debug("Поиск всех заказов");
        return  super.findAll();
    }
    @Override
    public BookOrder find(Integer id) {
        logger.debug("Поиск заказа с id: {}",id);
        return  super.find(id);
    }
    @Override
    public void delete(Integer id) {
        logger.debug("Удаление заказа с id: {}",id);
        super.delete(id);
    }

}

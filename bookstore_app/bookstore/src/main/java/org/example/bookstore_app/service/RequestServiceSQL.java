package org.example.bookstore_app.service;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.dao.RequestDAO;
import org.example.bookstore_app.model.Request;
import org.example.bookstore_app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class RequestServiceSQL extends GenericServiceImpl<Request, Integer, RequestDAO> {
    private static final Logger logger = LoggerFactory.getLogger(RequestServiceSQL.class);

    @Inject
    public RequestServiceSQL(RequestDAO requestDAO) {
        super(requestDAO);
    }
    @Override
    public Integer save(Request request) {
        logger.debug("Сохранение заявки");
        return  super.save(request);
    }

    @Override
    public void update(Request request) {
        logger.debug("Сохранение изменения заявки с id: {}",request.getId());
        super.update(request);
    }
    @Override
    public List<Request> findAll() {
        logger.debug("Поиск всех заявок");
        try {
            return super.findAll();
        } catch (Exception e) {
            logger.error("Ошибка при поиске всех заявок", e);
            return List.of(); // Возвращаем пустой список при ошибке
        }
    }
    @Override
    public Request find(Integer id) {
        logger.debug("Поиск заявки с id: {}",id);
        return  super.find(id);
    }
    @Override
    public void delete(Integer id) {
        logger.debug("Удаление заявки с id: {}",id);
        super.delete(id);
    }
    public Request findByIdOrderItem(int orderItemId){
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            Request entitys = defaultRepository.findByIdOrderItem(orderItemId);
            transaction.commit();
            return entitys;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Ошибка при поиске заявки с id заказа: "+orderItemId, e);
        } finally {
            HibernateUtil.closeSession();
        }
    }

}

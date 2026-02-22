package org.example.bookstore_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.example.bookstore_app.dao.RequestDAO;
import org.example.bookstore_app.model.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
@Qualifier("requestServiceSQL")
public class RequestServiceSQL extends GenericServiceImpl<Request, Integer, RequestDAO>
implements IRequestService{
    private static final Logger logger = LoggerFactory.getLogger(RequestServiceSQL.class);

    @Autowired
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
       logger.debug("Поиск заявки по idOrderItem: {}",orderItemId);
       return defaultRepository.findByIdOrderItem(orderItemId);
    }
    public List<Request> findByRequestIdWithBook(Integer idBook) {
        logger.debug("Поиск заявок по книге idBook: {}", idBook);
        return defaultRepository.findByRequestIdWithBook(idBook);
    }
}

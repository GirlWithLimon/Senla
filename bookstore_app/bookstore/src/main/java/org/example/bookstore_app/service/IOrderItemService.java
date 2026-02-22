package org.example.bookstore_app.service;

import org.example.bookstore_app.model.BookOrderItem;

import java.util.List;

public interface IOrderItemService extends GenericService<BookOrderItem, Integer>{
    List<BookOrderItem> findByOrderId(Integer idOrder);
    List<BookOrderItem> findByOrderIdWithBooks(Integer idOrder);
    double findSumByIdOrder(Integer idOrder);
    List<BookOrderItem> findByOrderIdWithAllData(Integer idOrder);
}

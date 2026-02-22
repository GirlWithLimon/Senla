package org.example.bookstore_app.service;

import org.example.bookstore_app.model.Request;

import java.util.List;

public interface IRequestService extends GenericService<Request, Integer>{
    Request findByIdOrderItem(int orderItemId);
    List<Request> findByRequestIdWithBook(Integer idBook);
}

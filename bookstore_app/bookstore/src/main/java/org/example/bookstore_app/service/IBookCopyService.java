package org.example.bookstore_app.service;

import org.example.bookstore_app.model.BookCopy;

import java.util.List;

public interface IBookCopyService extends GenericService<BookCopy, Integer>{
    List<BookCopy> findWithBookId();
    List<BookCopy> findByBookId(int idBook);
    int findCountByIdBook(Integer idBook);
}

package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
public class StokService implements Serializable {
    @Inject
    BookDAO bookDAO;
    @Inject
    BookCopyDAO bookCopyDAO;
    @Inject
    BookOrderDAO bookOrderDAO;
    @Inject
    BookOrderItemDAO bookOrderItemDAO;
    @Inject
    RequestDAO requestDAO;


    //получение данных из таблиц полностью
    public List<Book> getBooks() { return new ArrayList<>(bookDAO.findAll()); }
    public List<BookCopy> getBooksCopy() {
        return new ArrayList<>(bookCopyDAO.findAll());
    }
    public List<BookOrder> getOrders() {
        return new ArrayList<>(bookOrderDAO.findAll());
    }
    public List<BookOrderItem> getBookOrderItem() {
        return new ArrayList<>(bookOrderItemDAO.findAll());
    }
    public List<Request> getRequests() {
        return new ArrayList<>(requestDAO.findAll());
    }
    //получение по своим ид
    public Book getBooksById(Integer id) { return bookDAO.findById(id); }
    public BookCopy getBooksCopyByID(Integer id) {
        return bookCopyDAO.findById(id);
    }
    public BookOrder getOrderByID(Integer id) {
        return bookOrderDAO.findById(id);
    }
    public BookOrderItem getBookOrderItemByID(Integer id) {
        return bookOrderItemDAO.findById(id);
    }
    public Request getRequestsById(Integer id) {
        return requestDAO.findById(id);
    }
    //получение по каким-то другим ид
    public List<BookCopy> getBookCopyByBookId(Integer idBook){return bookCopyDAO.findByBookId(idBook);}
    public List<BookOrderItem> getBookOrderItemByidOrder(Integer idOrder) {
        return new ArrayList<>(bookOrderItemDAO.findByOrderId(idOrder));
    }
    public List<Request> getRequestsByidOrderItem(Integer idOrderItem) {
        return new ArrayList<>(requestDAO.findByIdOrderItem(idOrderItem));
    }
    public int findCountByIdBook(Integer idBook){
        return  bookCopyDAO.findCountByIdBook(idBook);
    }


    //Добавление новых значений (если такое уже было, то изменение)
    public Book addBook(Book book){return bookDAO.save(book);    }
    public BookCopy addBooksCopy(BookCopy copy){
        return bookCopyDAO.save(copy);
    }
    public BookOrder addOrder(BookOrder order){
        return bookOrderDAO.save(order);
    }
    public BookOrderItem addBookOrderItem(BookOrderItem orderItem) {
        return bookOrderItemDAO.save(orderItem);
    }
    public Request addRequest(Request request){
        return requestDAO.save(request);
    }


    //удаление полей по ид
    public void removeBook(Book book){
        bookDAO.deleteById(book.getId());
    }
    public void removeBooksCopy(BookCopy copy){
        bookCopyDAO.deleteById(copy.getId());
    }
    public void removeOrder(BookOrder order){
        bookOrderDAO.deleteById(order.getId());
    }
    public void removeOrderItem(BookOrderItem orderItem){
        bookOrderItemDAO.deleteById(orderItem.getId());
    }
    public void removeRequest(Request request){
        requestDAO.deleteById(request.getId());
    }


    //изменения полей


}
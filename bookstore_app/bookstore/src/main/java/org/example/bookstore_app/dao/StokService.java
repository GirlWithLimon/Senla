package org.example.bookstore_app.dao;

import org.example.bookstore_app.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StokService implements Serializable {

    BookDAO bookDAO = new BookDAO();
    BookCopyDAO bookCopyDAO = new BookCopyDAO();
    BookOrderDAO bookOrderDAO = new BookOrderDAO();
    BookOrderItemDAO bookOrderItemDAO = new BookOrderItemDAO();
    RequestDAO requestDAO = new RequestDAO();

    //получение данных из таблиц полностью
    public List<Book> getBooks() { return new ArrayList<>(bookDAO.findAll()); }
    public List<BookCopy> getBooksCopy() {
        return new ArrayList<>(bookCopyDAO.findAll());
    }
    public List<BookOrder> getOrders() {
        return new ArrayList<>(bookOrderDAO.findAll());
    }
    public List<BookOrderItem> getBookOrderItem(Integer idOrder) {
        return new ArrayList<>(bookOrderItemDAO.findByOrderId(idOrder));
    }
    public List<Request> getRequests() {
        return new ArrayList<>(requestDAO.findAll());
    }
    //получение по своим ид
    public Book getBooksById(Integer id) { return bookDAO.findById(id); }
    public BookCopy getBooksCopyByID(Integer id) {
        return bookCopyDAO.findById(id);
    }
    public BookOrder getOrders(Integer id) {
        return bookOrderDAO.findById(id);
    }
    public BookOrderItem getBookOrderItemByID(Integer id) {
        return bookOrderItemDAO.findById(id);
    }
    public Request getRequestsById(Integer id) {
        return requestDAO.findById(id);
    }
    //получение по каким-то другим ид
    public List<BookOrderItem> getBookOrderItemByidOrder(Integer idOrder) {
        return new ArrayList<>(bookOrderItemDAO.findByOrderId(idOrder));
    }
    public List<Request> getRequestsByidOrderItem(Integer idOrderItem) {
        return new ArrayList<>(requestDAO.findByidOrderItem(idOrderItem));
    }

    



    //Добавление новых значений (если такое уже было, то изменение)
    public void addBook(Book book){
       bookDAO.save(book);
    }
    public void addBooksCopy(BookCopy copy){
        bookCopyDAO.save(copy);
    }
    public void addOrder(BookOrder order){
        bookOrderDAO.save(order);
    }
    public void addBookToOrder(BookOrderItem orderItem) {
        bookOrderItemDAO.save(orderItem);
    }
    public void addRequest(Request request){
        requestDAO.save(request);
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
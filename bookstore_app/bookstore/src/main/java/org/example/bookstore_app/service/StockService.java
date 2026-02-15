package org.example.bookstore_app.service;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.*;
import org.example.bookstore_app.util.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
public class StockService implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(StockService.class);
    @Inject
    BookServiceSQL bookService;
    @Inject
    BookCopyServiceSQL bookCopyService;
    @Inject
    OrderServiceSQL bookOrderDAO;
    @Inject
    OrderItemServiceSQL bookOrderItemService;
    @Inject
    RequestServiceSQL requestServiceSQL;
    private static StockService instance;

    public static StockService getInstance() {
        if (instance == null) {
            instance = new StockService();
        }
        return instance;
    }

    //получение данных из таблиц полностью
    public List<Book> getBooks() { return new ArrayList<>(bookService.findAll()); }
    public List<BookCopy> getBooksCopy() {
        return new ArrayList<>(bookCopyService.findAll());
    }
    public List<BookOrder> getOrders() {
        return new ArrayList<>(bookOrderDAO.findAll());
    }
    public List<BookOrderItem> getBookOrderItem() {
        return new ArrayList<>(bookOrderItemService.findAll());
    }
    public List<Request> getRequests() {
        return new ArrayList<>(requestServiceSQL.findAll());
    }
    //получение по своим ид
    public Book getBooksById(Integer id) { return bookService.find(id); }
    public BookCopy getBooksCopyByID(Integer id) {
        return bookCopyService.find(id);
    }
    public BookOrder getOrderByID(Integer id) {
        return bookOrderDAO.find(id);
    }
    public BookOrderItem getBookOrderItemByID(Integer id) {
        return bookOrderItemService.find(id);
    }
    public Request getRequestsById(Integer id) {
        return requestServiceSQL.find(id);
    }
    //получение по каким-то другим ид
    public List<BookCopy> getBookCopyByBookId(Integer idBook){return bookCopyService.findByBookId(idBook);}
    public List<BookOrderItem> getBookOrderItemByidOrder(Integer idOrder) {
        return new ArrayList<>(bookOrderItemService.findByOrderId(idOrder));
    }
    public Request getRequestsByidOrderItem(Integer idOrderItem) {
        return requestServiceSQL.findByIdOrderItem(idOrderItem);
    }
    public int findCountByIdBook(Integer idBook){
        return  bookCopyService.findCountByIdBook(idBook);
    }
    public List<BookCopy> findWithBookId(){return bookCopyService.findWithBookId();}
    public List<BookOrderItem> getBookOrderItemByidOrderWithBooks(Integer idOrder) {
        return new ArrayList<>(bookOrderItemService.findByOrderIdWithBooks(idOrder));
    }

    //Добавление новых значений
    public void addBook(Book book){
        bookService.save(book);
    }
    public void addBooksCopy(BookCopy copy){
        bookCopyService.save(copy);
    }
    public void addOrder(BookOrder order){
        bookOrderDAO.save(order);
    }
    public BookOrderItem addBookOrderItem(BookOrderItem orderItem) {
        return bookOrderItemService.find(bookOrderItemService.save(orderItem));
    }
    public Request addRequest(Request request){
        return requestServiceSQL.find(requestServiceSQL.save(request));
    }


    //удаление полей по ид
    public void removeBook(Book book){
        bookService.delete(book.getId());
    }
    public void removeBooksCopy(BookCopy copy){
        bookCopyService.delete(copy.getId());
    }
    public void removeOrder(BookOrder order){
        bookOrderDAO.delete(order.getId());
    }
    public void removeOrderItem(BookOrderItem orderItem){
        bookOrderItemService.delete(orderItem.getId());
    }
    public void removeRequest(Request request){
        requestServiceSQL.delete(request.getId());
    }


    //изменения полей
    public void updateBook(Book book){
        bookService.update(book);
    }
    public void updateBooksCopy(BookCopy copy){
        bookCopyService.update(copy);
    }
    public void updateOrder(BookOrder order){
        bookOrderDAO.update(order);
    }
    public void updateBookOrderItem(BookOrderItem orderItem) {
        bookOrderItemService.update(orderItem);
    }
    public void refreshCache() {
        Session session = HibernateUtil.getCurrentSession();
        session.clear(); // Очищает кэш первого уровня
        logger.debug("Кэш Hibernate очищен");
    }
}

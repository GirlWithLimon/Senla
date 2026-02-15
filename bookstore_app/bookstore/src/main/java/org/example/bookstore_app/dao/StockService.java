package org.example.bookstore_app.dao;

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
    BookDAO bookDAO;
    @Inject
    BookCopyDAO bookCopyDAO;
    @Inject
    BookOrderDAO bookOrderDAO;
    @Inject
    BookOrderItemDAO bookOrderItemDAO;
    @Inject
    RequestDAO requestDAO;
    private static StockService instance;

    public static StockService getInstance() {
        if (instance == null) {
            instance = new StockService();
        }
        return instance;
    }

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
    public Book getBooksById(Integer id) { return bookDAO.find(id); }
    public BookCopy getBooksCopyByID(Integer id) {
        return bookCopyDAO.find(id);
    }
    public BookOrder getOrderByID(Integer id) {
        return bookOrderDAO.find(id);
    }
    public BookOrderItem getBookOrderItemByID(Integer id) {
        return bookOrderItemDAO.find(id);
    }
    public Request getRequestsById(Integer id) {
        return requestDAO.find(id);
    }
    //получение по каким-то другим ид
    public List<BookCopy> getBookCopyByBookId(Integer idBook){return bookCopyDAO.findByBookId(idBook);}
    public List<BookOrderItem> getBookOrderItemByidOrder(Integer idOrder) {
        return new ArrayList<>(bookOrderItemDAO.findByOrderId(idOrder));
    }
    public Request getRequestsByidOrderItem(Integer idOrderItem) {
        return requestDAO.findByIdOrderItem(idOrderItem);
    }
    public int findCountByIdBook(Integer idBook){
        return  bookCopyDAO.findCountByIdBook(idBook);
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
    public BookOrderItem addBookOrderItem(BookOrderItem orderItem) {
        return bookOrderItemDAO.find(bookOrderItemDAO.save(orderItem));
    }
    public Request addRequest(Request request){
        return requestDAO.find(requestDAO.save(request));
    }


    //удаление полей по ид
    public void removeBook(Book book){
        bookDAO.delete(book.getId());
    }
    public void removeBooksCopy(BookCopy copy){
        bookCopyDAO.delete(copy.getId());
    }
    public void removeOrder(BookOrder order){
        bookOrderDAO.delete(order.getId());
    }
    public void removeOrderItem(BookOrderItem orderItem){
        bookOrderItemDAO.delete(orderItem.getId());
    }
    public void removeRequest(Request request){
        requestDAO.delete(request.getId());
    }


    //изменения полей
    public void updateBook(Book book){
        bookDAO.update(book);
    }
    public void updateBooksCopy(BookCopy copy){
        bookCopyDAO.update(copy);
    }
    public void updateOrder(BookOrder order){
        bookOrderDAO.update(order);
    }
    public void updateBookOrderItem(BookOrderItem orderItem) {
        bookOrderItemDAO.update(orderItem);
    }
    public void refreshCache() {
        Session session = HibernateUtil.getCurrentSession();
        session.clear(); // Очищает кэш первого уровня
        logger.debug("Кэш Hibernate очищен");
    }
}

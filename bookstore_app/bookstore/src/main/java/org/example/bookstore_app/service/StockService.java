package org.example.bookstore_app.service;

import org.example.bookstore_app.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StockService implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private IBookService bookService;

    @Autowired
    private IBookCopyService bookCopyService;

    @Autowired
    private IOrderService bookOrderService;

    @Autowired
    private IOrderItemService bookOrderItemService;

    @Autowired
    private IRequestService requestServiceSQL;

    //получение данных из таблиц полностью
    @Transactional(readOnly = true)
    public List<Book> getBooks() {
        return new ArrayList<>(bookService.findAll());
    }

    @Transactional(readOnly = true)
    public List<BookCopy> getBooksCopy() {
        return new ArrayList<>(bookCopyService.findAll());
    }

    @Transactional(readOnly = true)
    public List<BookOrder> getOrders() {
        return new ArrayList<>(bookOrderService.findAll());
    }

    @Transactional(readOnly = true)
    public List<BookOrderItem> getBookOrderItem() {
        return new ArrayList<>(bookOrderItemService.findAll());
    }

    @Transactional(readOnly = true)
    public List<Request> getRequests() {
        return new ArrayList<>(requestServiceSQL.findAll());
    }

    //получение по своим ид
    @Transactional(readOnly = true)
    public Book getBooksById(Integer id) {
        return bookService.find(id);
    }

    @Transactional(readOnly = true)
    public BookCopy getBooksCopyByID(Integer id) {
        return bookCopyService.find(id);
    }

    @Transactional(readOnly = true)
    public BookOrder getOrderByID(Integer id) {
        return bookOrderService.find(id);
    }

    @Transactional(readOnly = true)
    public BookOrderItem getBookOrderItemByID(Integer id) {
        return bookOrderItemService.find(id);
    }

    @Transactional(readOnly = true)
    public Request getRequestsById(Integer id) {
        return requestServiceSQL.find(id);
    }

    //получение по каким-то другим ид
    @Transactional(readOnly = true)
    public List<BookCopy> getBookCopyByBookId(Integer idBook){
        return bookCopyService.findByBookId(idBook);
    }

    @Transactional(readOnly = true)
    public List<BookOrderItem> getBookOrderItemByidOrder(Integer idOrder) {
        return new ArrayList<>(bookOrderItemService.findByOrderId(idOrder));
    }

    @Transactional(readOnly = true)
    public Request getRequestsByidOrderItem(Integer idOrderItem) {
        return requestServiceSQL.findByIdOrderItem(idOrderItem);
    }

    @Transactional(readOnly = true)
    public int findCountByIdBook(Integer idBook){
        return bookCopyService.findCountByIdBook(idBook);
    }

    @Transactional(readOnly = true)
    public List<BookCopy> findWithBookId(){
        return bookCopyService.findWithBookId();
    }

    @Transactional(readOnly = true)
    public List<BookOrderItem> getBookOrderItemByidOrderWithBooks(Integer idOrder) {
        return new ArrayList<>(bookOrderItemService.findByOrderIdWithBooks(idOrder));
    }

    @Transactional(readOnly = true)
    public double findSumByIdOrder(Integer idOrder) {
        return bookOrderItemService.findSumByIdOrder(idOrder);
    }

    @Transactional(readOnly = true)
    public List<BookOrderItem> findByOrderIdWithAllData(Integer idOrder) {
        return bookOrderItemService.findByOrderIdWithAllData(idOrder);
    }

    @Transactional(readOnly = true)
    public List<Request> findByRequestIdWithBook(Integer idBook) {
        return requestServiceSQL.findByRequestIdWithBook(idBook);
    }

    //Добавление новых значений
    @Transactional
    public void addBook(Book book){
        bookService.save(book);
    }

    @Transactional
    public void addBooksCopy(BookCopy copy){
        bookCopyService.save(copy);
    }

    @Transactional
    public BookOrder addOrder(BookOrder order){
        return bookOrderService.find(bookOrderService.save(order));
    }

    @Transactional
    public BookOrderItem addBookOrderItem(BookOrderItem orderItem) {
        return bookOrderItemService.find(bookOrderItemService.save(orderItem));
    }

    @Transactional
    public Request addRequest(Request request){
        return requestServiceSQL.find(requestServiceSQL.save(request));
    }

    //удаление полей по ид
    @Transactional
    public void removeBook(Book book){
        bookService.delete(book.getId());
    }

    @Transactional
    public void removeBooksCopy(BookCopy copy){
        bookCopyService.delete(copy.getId());
    }

    @Transactional
    public void removeOrder(BookOrder order){
        bookOrderService.delete(order.getId());
    }

    @Transactional
    public void removeOrderItem(BookOrderItem orderItem){
        bookOrderItemService.delete(orderItem.getId());
    }

    @Transactional
    public void removeRequest(Request request){
        requestServiceSQL.delete(request.getId());
    }

    //изменения полей
    @Transactional
    public void updateBook(Book book){
        bookService.update(book);
    }

    @Transactional
    public void updateBooksCopy(BookCopy copy){
        bookCopyService.update(copy);
    }

    @Transactional
    public void updateOrder(BookOrder order){
        bookOrderService.update(order);
    }

    @Transactional
    public void updateBookOrderItem(BookOrderItem orderItem) {
        bookOrderItemService.update(orderItem);
    }
}
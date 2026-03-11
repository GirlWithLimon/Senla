package org.example.bookstore_app.controller.rest;

import jakarta.validation.Valid;
import org.example.bookstore_app.dto.OrderItemDTO;
import org.example.bookstore_app.dto.OrderRequestDTO;
import org.example.bookstore_app.dto.OrderResponseDTO;
import org.example.bookstore_app.exception.OrderNotFoundException;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookOrder;
import org.example.bookstore_app.model.BookOrderItem;
import org.example.bookstore_app.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders/sort")
public class InformationOrderController {

    private static final Logger logger = LoggerFactory.getLogger(InformationOrderController.class);

    @Autowired
    private StockService stockService;

    @Autowired
    private org.example.bookstore_app.controller.OrdersController ordersController;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        logger.info("GET /api/orders - запрос на получение всех заказов");

        List<BookOrder> orders = stockService.getOrders();
        List<OrderResponseDTO> dtos = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable("id") int id) {
        logger.info("GET /api/orders/{} - запрос на получение заказа", id);

        BookOrder order = stockService.getOrderByID(id);
        if (order == null) {
            throw new OrderNotFoundException("Заказ с ID " + id + " не найден");
        }

        return ResponseEntity.ok(convertToDTO(order));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
        logger.info("POST /api/orders - создание заказа для клиента: {}", orderRequest.getCustomerName());

        List<Book> books = orderRequest.getBookIds().stream()
                .map(id -> stockService.getBooksById(id))
                .filter(book -> book != null)
                .collect(Collectors.toList());

        if (books.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        BookOrder order = ordersController.createOrder(
                books,
                orderRequest.getCustomerName(),
                orderRequest.getCustomerContact()
        );

        logger.info("Заказ создан с ID: {}", order.getId());
        return new ResponseEntity<>(convertToDTO(order), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable("id") int id) {
        logger.info("DELETE /api/orders/{} - отмена заказа", id);

        BookOrder order = stockService.getOrderByID(id);
        if (order == null) {
            throw new OrderNotFoundException("Заказ с ID " + id + " не найден");
        }

        ordersController.cancelOrder(id);
        logger.info("Заказ с ID {} отменен", id);

        return ResponseEntity.noContent().build();
    }

    private OrderResponseDTO convertToDTO(BookOrder order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerContact(order.getCustomerContact());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus().name());
        dto.setTotalPrice(order.getTotalPrice());

        List<OrderItemDTO> items = stockService.getBookOrderItemByidOrder(order.getId()).stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());

        dto.setItems(items);
        return dto;
    }

    private OrderItemDTO convertToItemDTO(BookOrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setBookId(item.getBook().getId());
        dto.setBookName(item.getBook().getName());
        dto.setStatus(item.getStatus().name());
        dto.setPrice(item.getBook().getPrice());
        return dto;
    }
}
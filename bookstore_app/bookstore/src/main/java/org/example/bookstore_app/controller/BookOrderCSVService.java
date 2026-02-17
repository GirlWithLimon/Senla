package org.example.bookstore_app.controller;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.BookOrder;
import org.example.bookstore_app.model.OrderStatus;
import org.example.bookstore_app.service.StockService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Component
public class BookOrderCSVService implements ICSVImportExport<BookOrder> {
    @Inject
    private StockService stockService;

    
    public BookOrderCSVService() { }
    
    @Override
    public String getFileName() {
        return "orders";
    }
    
    @Override
    public String getHeaders() {
        return "ID,CustomerName,CustomerContact,OrderDate,Status,TotalPrice";
    }
    
    @Override
    public String toCSV(BookOrder order) {
        return String.join(",",
            escape(Integer.toString(order.getId())),
            escape(order.getCustomerName()),
            escape(order.getCustomerContact()),
            order.getOrderDate().toString(),
            order.getStatus().name(),
            String.valueOf(order.getTotalPrice())
        );
    }
    
    @Override
    public BookOrder fromCSV(String csvLine) {
        String[] parts = parseCSVLine(csvLine);
        String id = unescape(parts[0]);
        String customerName = unescape(parts[1]);
        String customerContact = unescape(parts[2]);
        LocalDate orderDate = LocalDate.parse(parts[3]);
        OrderStatus status = OrderStatus.valueOf(parts[4]);
        double totalPrice = Double.parseDouble(parts[5]);
        
        BookOrder order = new BookOrder(Integer.parseInt(id), customerName, customerContact);
        order.setStatus(status);
        stockService.addOrder(order);
        return order;
    }
    
    @Override
    public List<BookOrder> getAllEntities() {
        return (List<BookOrder>) stockService.getOrders();
    }
    
    @Override
    public void saveEntities(List<BookOrder> importedOrders) {
        List<BookOrder> currentOrders = (List<BookOrder>) stockService.getOrders();
        
        Map<Integer, BookOrder> importedOrdersMap = importedOrders.stream()
            .collect(Collectors.toMap(BookOrder::getId, order -> order));

        for (BookOrder currentOrder : currentOrders) {
            BookOrder importedOrder = importedOrdersMap.get(currentOrder.getId());

            if (importedOrder != null) {
                updateOrderData(currentOrder, importedOrder);
                importedOrdersMap.remove(currentOrder.getId());
            }
        }
        
        currentOrders.addAll(importedOrdersMap.values());
        
        System.out.println("Импорт заказов завершен. Обработано: " + importedOrders.size() + 
                          " заказов. Всего в системе: " + currentOrders.size() + " заказов.");
    }
    
    private void updateOrderData(BookOrder currentOrder, BookOrder importedOrder) {
        currentOrder.setCustomerName(importedOrder.getCustomerName());
        currentOrder.setCustomerContact(importedOrder.getCustomerContact());
        currentOrder.setStatus(importedOrder.getStatus());
        
    }
    
    private String escape(String field) {
        if (field == null) return "";
        return "\"" + field.replace("\"", "\"\"") + "\"";
    }
    
    private String unescape(String field) {
        if (field.startsWith("\"") && field.endsWith("\"")) {
            return field.substring(1, field.length() - 1).replace("\"\"", "\"");
        }
        return field;
    }
    
    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        result.add(field.toString());
        
        return result.toArray(new String[0]);
    }
}

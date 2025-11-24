package project.controller;

import project.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookOrderCSVService implements ICSVImportExport<BookOrder> {
    private final Stok stok;
    private final ID idGenerator;
    
    public BookOrderCSVService(Stok stok) {
        this.stok = stok;
        this.idGenerator = new ID();
    }
    
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
            escape(order.getId()),
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
        
        BookOrder order = new BookOrder(id, customerName, customerContact);
        order.setStatus(status);
        // Note: Order items will need separate import/export
        
        return order;
    }
    
    @Override
    public List<BookOrder> getAllEntities() {
        return stok.getOrders();
    }
    
    @Override
    public void saveEntities(List<BookOrder> importedOrders) {
        List<BookOrder> currentOrders = stok.getOrders();
        
        // Создаем карту импортированных заказов по ID
        Map<String, BookOrder> importedOrdersMap = importedOrders.stream()
            .collect(Collectors.toMap(BookOrder::getId, order -> order));
        
        // Обновляем существующие заказы
        for (int i = 0; i < currentOrders.size(); i++) {
            BookOrder currentOrder = currentOrders.get(i);
            BookOrder importedOrder = importedOrdersMap.get(currentOrder.getId());
            
            if (importedOrder != null) {
                // Обновляем существующий заказ
                updateOrderData(currentOrder, importedOrder);
                importedOrdersMap.remove(currentOrder.getId());
            }
        }
        
        // Добавляем новые заказы
        currentOrders.addAll(importedOrdersMap.values());
        
        System.out.println("Импорт заказов завершен. Обработано: " + importedOrders.size() + 
                          " заказов. Всего в системе: " + currentOrders.size() + " заказов.");
    }
    
    private void updateOrderData(BookOrder currentOrder, BookOrder importedOrder) {
        // Обновляем поля заказа
        currentOrder.setCustomerName(importedOrder.getCustomerName());
        currentOrder.setCustomerContact(importedOrder.getCustomerContact());
        currentOrder.setStatus(importedOrder.getStatus());
        
        // Примечание: Order items не импортируются через CSV
        // Для полного импорта нужно реализовать отдельный сервис для BookOrderItem
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
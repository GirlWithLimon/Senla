package org.example.bookstore_app.controller;

import org.example.annotation.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ImportExportService {
    private final Map<String, ICSVImportExport<?>> services;
    
    public ImportExportService() {
       this.services = new HashMap<>();
        initializeServices();
    }
    
    private void initializeServices() {
        services.put("books", new BookCSVService());
        services.put("orders", new BookOrderCSVService());
    }
    
    public void exportEntities(String entityType, String filePath) throws IOException {
        ICSVImportExport<?> service = services.get(entityType.toLowerCase());
        if (service == null) {
            throw new IllegalArgumentException("Неизвестный тип сущности: " + entityType);
        }
        
        CSVUtils.exportToCSV(service, filePath);
    }
    
    public void importEntities(String entityType, String filePath) throws IOException {
        ICSVImportExport<?> service = services.get(entityType.toLowerCase());
        if (service == null) {
            throw new IllegalArgumentException("Неизвестный тип сущности: " + entityType);
        }
        
        CSVUtils.importFromCSV(service, filePath);
    }
    
    public String getAvailableEntityTypes() {
        return String.join(", ", services.keySet());
    }
}

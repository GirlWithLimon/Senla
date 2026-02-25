package org.example.bookstore_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ImportExportService {
    private final Map<String, ICSVImportExport<?>> services;
    @Autowired
    private BookCSVService bookCSVService;
    @Autowired
    private BookOrderCSVService bookOrderCSVService;

    public ImportExportService() {
        this.services = new HashMap<>();
    }

    @PostConstruct
    private void initializeServices() {
        services.put("books", bookCSVService);
        services.put("orders", bookOrderCSVService);
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

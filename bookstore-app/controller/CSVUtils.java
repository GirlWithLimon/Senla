package project.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {
    
    public static <T> void exportToCSV(ICSVImportExport<T> service, String filePath) throws IOException {
        List<T> entities = service.getAllEntities();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(service.getHeaders());
            for (T entity : entities) {
                writer.println(service.toCSV(entity));
            }
        }
    }
    
    public static <T> void importFromCSV(ICSVImportExport<T> service, String filePath) throws IOException {
        List<T> importedEntities = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); 
            
            if (line == null || !line.equals(service.getHeaders())) {
                System.out.println("Предупреждение: заголовки CSV не совпадают с ожидаемыми");
            }
            
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.trim().isEmpty()) {
                    try {
                        T entity = service.fromCSV(line);
                        importedEntities.add(entity);
                    } catch (Exception e) {
                        System.out.println("Ошибка в строке " + lineNumber + ": " + e.getMessage());
                    }
                }
            }
        }
        
        service.saveEntities(importedEntities);
        
        System.out.println("Успешно импортировано сущностей: " + importedEntities.size());
    }
}
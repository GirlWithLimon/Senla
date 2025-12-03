package bookstore_app.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final String CONFIG_FILE = "bookstore.properties";
    private static Config instance;
    
    private Properties properties;
    private int monthsForOldBook;
    private boolean autoCompleteRequests;
    
    private Config() {
        loadConfig();
    }
    
    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }
    
    private void loadConfig() {
        properties = new Properties();
        
        properties.setProperty("months.for.old.book", "6");
        properties.setProperty("auto.complete.requests", "true");
        
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            System.out.println("Конфигурация загружена из файла: " + CONFIG_FILE);
        } catch (IOException e) {
            System.out.println("Файл конфигурации не найден. Используются значения по умолчанию.");
        }
        
        monthsForOldBook = Integer.parseInt(properties.getProperty("months.for.old.book"));
        autoCompleteRequests = Boolean.parseBoolean(properties.getProperty("auto.complete.requests"));
    }
    
    public int getMonthsForOldBook() {
        return monthsForOldBook;
    }
    
    public boolean isAutoCompleteRequests() {
        return autoCompleteRequests;
    }
    
    public void saveConfig() {
        properties.setProperty("months.for.old.book", String.valueOf(monthsForOldBook));
        properties.setProperty("auto.complete.requests", String.valueOf(autoCompleteRequests));
        
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Bookstore Application Configuration");
            System.out.println("Конфигурация сохранена в файл: " + CONFIG_FILE);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении конфигурации: " + e.getMessage());
        }
    }
    
    public void setMonthsForOldBook(int months) {
        this.monthsForOldBook = months;
    }
    
    public void setAutoCompleteRequests(boolean autoComplete) {
        this.autoCompleteRequests = autoComplete;
    }
}

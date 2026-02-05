package org.example.bookstore_app.controller;

import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.config.DI;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static Config instance;
    private BookstoreConfig bookstoreConfig;

    private Config() {
        this.bookstoreConfig = DI.getConfig(BookstoreConfig.class);
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public int getMonthsForOldBook() {
        return bookstoreConfig.getMonthsForOldBook();
    }

    public boolean isAutoCompleteRequests() {
        return bookstoreConfig.isAutoCompleteRequests();
    }


    public String getCsvDelimiter() {
        return bookstoreConfig.getCsvDelimiter();
    }



    public void saveConfig() {
        Properties props = new Properties();

        try (FileOutputStream fos = new FileOutputStream("bookstore.properties")) {
            props.setProperty("months.for.old.book",
                    String.valueOf(bookstoreConfig.getMonthsForOldBook()));
            props.setProperty("auto.complete.requests",
                    String.valueOf(bookstoreConfig.isAutoCompleteRequests()));

            if (bookstoreConfig.getCsvDelimiter() != null) {
                props.setProperty("csv.delimiter", bookstoreConfig.getCsvDelimiter());
            }


            props.store(fos, "Bookstore Application Configuration");
            System.out.println("Конфигурация сохранена в файл: bookstore.properties");

            DI.reloadConfig(BookstoreConfig.class);

        } catch (IOException e) {
            System.out.println("Ошибка при сохранении конфигурации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        DI.reloadConfig(BookstoreConfig.class);

        this.bookstoreConfig = DI.getConfig(BookstoreConfig.class);

        if (instance != null) {
            instance = new Config();
        }
    }

    public void setMonthsForOldBook(int months) {
        if (months > 0) {
            bookstoreConfig.setMonthsForOldBook(months);
        } else {
            System.out.println("Ошибка: значение должно быть больше 0");
        }
    }

    public void setAutoCompleteRequests(boolean autoComplete) {
        bookstoreConfig.setAutoCompleteRequests(autoComplete);
    }

    public void setCsvDelimiter(String delimiter) {
        if (delimiter != null && !delimiter.isEmpty()) {
            bookstoreConfig.setCsvDelimiter(delimiter);
        }
    }


    public void loadFromFile(String configFile) {
        try {
            System.setProperty("config.file", configFile);

            DI.reloadConfig(BookstoreConfig.class);
            this.bookstoreConfig = DI.getConfig(BookstoreConfig.class);

            System.out.println("Конфигурация загружена из файла: " + configFile);
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке конфигурации из файла: " +
                    configFile + " - " + e.getMessage());
        }
    }

    public String getConfigAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Текущая конфигурация ===\n");
        sb.append("Месяцев для залежавшихся книг: ").append(getMonthsForOldBook()).append("\n");
        sb.append("Автовыполнение запросов: ").append(isAutoCompleteRequests() ? "Включено" : "Отключено").append("\n");
        sb.append("Разделитель CSV: ").append(getCsvDelimiter() != null ? getCsvDelimiter() : "(по умолчанию ,)").append("\n");
        return sb.toString();
    }

    public Properties getAllProperties() {
        Properties props = new Properties();
        props.setProperty("months.for.old.book", String.valueOf(getMonthsForOldBook()));
        props.setProperty("auto.complete.requests", String.valueOf(isAutoCompleteRequests()));

        if (getCsvDelimiter() != null) {
            props.setProperty("csv.delimiter", getCsvDelimiter());
        }


        return props;
    }

    public static void resetToDefaults() {
        instance = null;
        DI.reloadAllConfigs();
        System.out.println("Конфигурация сброшена к значениям по умолчанию");
    }
}

package org.example.bookstore_app.config;

import org.example.annotation.Component;
import org.example.annotation.ConfigProperty;
import org.example.annotation.PropertyType;

@Component
public class BookstoreConfig {

    @ConfigProperty(propertyName = "months.for.old.book", type = PropertyType.INTEGER)
    private int monthsForOldBook = 6;

    @ConfigProperty(propertyName = "auto.complete.requests", type = PropertyType.BOOLEAN)
    private boolean autoCompleteRequests = true;

    @ConfigProperty(propertyName = "csv.delimiter")
    private String csvDelimiter = ",";

    public int getMonthsForOldBook() {
        return monthsForOldBook;
    }

    public boolean isAutoCompleteRequests() {
        return autoCompleteRequests;
    }

    public String getCsvDelimiter() {
        return csvDelimiter;
    }


    public void setMonthsForOldBook(int monthsForOldBook) {
        this.monthsForOldBook = monthsForOldBook;
    }

    public void setAutoCompleteRequests(boolean autoCompleteRequests) {
        this.autoCompleteRequests = autoCompleteRequests;
    }

    public void setCsvDelimiter(String csvDelimiter) {
        this.csvDelimiter = csvDelimiter;
    }


    @Override
    public String toString() {
        return "BookstoreConfig{" +
                "monthsForOldBook=" + monthsForOldBook +
                ", autoCompleteRequests=" + autoCompleteRequests +
                ", csvDelimiter='" + csvDelimiter + '\'' +
                '}';
    }
}
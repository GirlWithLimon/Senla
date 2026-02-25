package org.example.bookstore_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class BookstoreConfig {

    @Value("${months.for.old.book}")
    private int monthsForOldBook;

    @Value("${auto.complete.requests}")
    private boolean autoCompleteRequests;

    @Value("${csv.delimiter}")
    private String csvDelimiter;

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
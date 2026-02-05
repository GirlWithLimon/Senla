package org.example.bookstore_app.config;

import org.example.annotation.Component;
import org.example.annotation.ConfigProperty;
import org.example.annotation.PropertyType;

/**
 * Конфигурация приложения книжного магазина.
 * Содержит настройки, загружаемые из properties файлов.
 */
@Component
public final class BookstoreConfig {
    /** Число месяцев для определения старой книги. */
    private static final int DEFAULT_MONTHS_FOR_OLD_BOOK = 6;

    /** Число месяцев для старых книг. */
    @ConfigProperty(propertyName = "months.for.old.book", type = PropertyType.INTEGER)
    private int monthsForOldBook = DEFAULT_MONTHS_FOR_OLD_BOOK;

    /** Флаг автоматического завершения запросов. */
    @ConfigProperty(propertyName = "auto.complete.requests", type = PropertyType.BOOLEAN)
    private boolean autoCompleteRequests = true;

    /** Разделитель для CSV файлов. */
    @ConfigProperty(propertyName = "csv.delimiter")
    private String csvDelimiter = ",";

    /**
     * Возвращает количество месяцев для определения старой книги.
     *
     * @return количество месяцев
     */
    public int getMonthsForOldBook() {
        return monthsForOldBook;
    }

    /**
     * Проверяет, включено ли автоматическое завершение запросов.
     *
     * @return true если автоматическое завершение включено
     */
    public boolean isAutoCompleteRequests() {
        return autoCompleteRequests;
    }

    /**
     * Возвращает разделитель для CSV файлов.
     *
     * @return разделитель CSV
     */
    public String getCsvDelimiter() {
        return csvDelimiter;
    }

    /**
     * Устанавливает количество месяцев для определения старой книги.
     *
     * @param newMonthsForOldBook новое значение
     */
    public void setMonthsForOldBook(final int newMonthsForOldBook) {
        this.monthsForOldBook = newMonthsForOldBook;
    }

    /**
     * Устанавливает флаг автоматического завершения запросов.
     *
     * @param newAutoCompleteRequests новое значение
     */
    public void setAutoCompleteRequests(final boolean newAutoCompleteRequests) {
        this.autoCompleteRequests = newAutoCompleteRequests;
    }

    /**
     * Устанавливает разделитель для CSV файлов.
     *
     * @param newCsvDelimiter новый разделитель
     */
    public void setCsvDelimiter(final String newCsvDelimiter) {
        this.csvDelimiter = newCsvDelimiter;
    }

    /**
     * Возвращает строковое представление конфигурации.
     *
     * @return строковое представление
     */
    @Override
    public String toString() {
        return "BookstoreConfig{"
                + "monthsForOldBook=" + monthsForOldBook
                + ", autoCompleteRequests=" + autoCompleteRequests
                + ", csvDelimiter='" + csvDelimiter + '\''
                + '}';
    }
}

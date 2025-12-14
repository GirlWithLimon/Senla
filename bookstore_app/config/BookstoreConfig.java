package bookstore_app.config;

import bookstore_app.config.ConfigProperty;
import bookstore_app.config.PropertyType;

public class BookstoreConfig {

    @ConfigProperty(propertyName = "months.for.old.book", type = PropertyType.INTEGER)
    private int monthsForOldBook = 6; // Значение по умолчанию

    @ConfigProperty(propertyName = "auto.complete.requests", type = PropertyType.BOOLEAN)
    private boolean autoCompleteRequests = true;

    @ConfigProperty(propertyName = "csv.delimiter")
    private String csvDelimiter = ",";

    @ConfigProperty(propertyName = "backup.enabled", type = PropertyType.BOOLEAN)
    private boolean backupEnabled = false;

    @ConfigProperty(propertyName = "backup.path")
    private String backupPath = "./backups/";

     public int getMonthsForOldBook() {
        return monthsForOldBook;
    }

    public boolean isAutoCompleteRequests() {
        return autoCompleteRequests;
    }

    public String getCsvDelimiter() {
        return csvDelimiter;
    }

    public boolean isBackupEnabled() {
        return backupEnabled;
    }

    public String getBackupPath() {
        return backupPath;
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

    public void setBackupEnabled(boolean backupEnabled) {
        this.backupEnabled = backupEnabled;
    }

    public void setBackupPath(String backupPath) {
        this.backupPath = backupPath;
    }


    @Override
    public String toString() {
        return "BookstoreConfig{" +
                "monthsForOldBook=" + monthsForOldBook +
                ", autoCompleteRequests=" + autoCompleteRequests +
                ", csvDelimiter='" + csvDelimiter + '\'' +
                ", backupEnabled=" + backupEnabled +
                ", backupPath='" + backupPath + '\'' +
                '}';
    }
}
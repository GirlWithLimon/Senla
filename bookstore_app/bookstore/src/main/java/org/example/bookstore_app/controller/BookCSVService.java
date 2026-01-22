package org.example.bookstore_app.controller;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.model.BookStatus;
import org.example.bookstore_app.dao.StokService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Component
public class BookCSVService implements ICSVImportExport<Book> {
    @Inject
    private StokService stokService;
    
    public BookCSVService() { }
    
    @Override
    public String getFileName() {
        return "books";
    }
    
    @Override
    public String getHeaders() {
        return "ID,Name,Author,Price,PublicationDate,Information,Status";
    }
    
    @Override
    public String toCSV(Book book) {
        return String.join(",",
            escape(Integer.toString(book.getId())),
            escape(book.getName()),
            escape(book.getAuthor()),
            String.valueOf(book.getPrice()),
            book.getPublicationDate().toString(),
            escape(book.getInfo()),
            book.getStatus().name()
        );
    }
    
    @Override
    public Book fromCSV(String csvLine) {
        String[] parts = parseCSVLine(csvLine);
        int id = Integer.parseInt(unescape(parts[0]));
        String name = unescape(parts[1]);
        String author = unescape(parts[2]);
        double price = Double.parseDouble(parts[3]);
        LocalDate publicationDate = LocalDate.parse(parts[4]);
        String information = unescape(parts[5]);
        BookStatus status = BookStatus.valueOf(parts[6]);
        
        Book book = new Book(id, name, author, price, information, publicationDate);
        if (status == BookStatus.IN_STOCK) {
            book.setStatusStok();
        } else {
            book.setStatusNo();
        }
        stokService.addBook(book);
        return book;
    }
    
    @Override
    public List<Book> getAllEntities() {
        return stokService.getBooks();
    }
    
    @Override
    public void saveEntities(List<Book> importedBooks) {
        List<Book> currentBooks = stokService.getBooks();
        
        Map<Integer, Book> importedBooksMap = importedBooks.stream()
            .collect(Collectors.toMap(Book::getId, book -> book));

        for (Book currentBook : currentBooks) {
            Book importedBook = importedBooksMap.get(currentBook.getId());

            if (importedBook != null) {
                updateBookData(currentBook, importedBook);
                importedBooksMap.remove(currentBook.getId());
            }
        }
        
        currentBooks.addAll(importedBooksMap.values());
        
        syncBookCopiesWithImportedBooks(importedBooks);
        
        System.out.println("Импорт книг завершен. Обработано: " + importedBooks.size() + 
                          " книг. Всего в системе: " + currentBooks.size() + " книг.");
    }
    
    private void updateBookData(Book currentBook, Book importedBook) {
        currentBook.setInfo(importedBook.getInfo());
        currentBook.setPrice(importedBook.getPrice());
        
        if (importedBook.getStatus() == BookStatus.IN_STOCK) {
            currentBook.setStatusStok();
        } else {
            currentBook.setStatusNo();
        }
        
    }
    
    private void syncBookCopiesWithImportedBooks(List<Book> importedBooks) {
        Map<Integer, Book> importedBooksMap = importedBooks.stream()
            .collect(Collectors.toMap(Book::getId, book -> book));
        
        List<BookCopy> copiesToRemove = new ArrayList<>();
        List<BookCopy> currentCopies = stokService.getBooksCopy();
        
        for (BookCopy copy : currentCopies) {
            int bookId = copy.getIdBook();
            if (!importedBooksMap.containsKey(bookId)) {
                copiesToRemove.add(copy);
            }
        }
        
        copiesToRemove.forEach(stokService::removeBooksCopy);
        
       for (BookCopy copy : currentCopies) {
            if (!copiesToRemove.contains(copy)) {
                Book updatedBook = importedBooksMap.get(copy.getIdBook());
                if (updatedBook != null) {
                    if (hasBookCopies(updatedBook.getId())) {
                        updatedBook.setStatusStok();
                    } else {
                        updatedBook.setStatusNo();
                    }
                }
            }
        }
        
        for (Book book : importedBooks) {
            if (hasBookCopies(book.getId())) {
                book.setStatusStok();
            } else {
                book.setStatusNo();
            }
        }
        
        if (!copiesToRemove.isEmpty()) {
            System.out.println("Заменено книг: " + copiesToRemove.size());
        }
    }
    
    private boolean hasBookCopies(int bookId) {
        return stokService.getBooksCopy().stream()
            .anyMatch(copy -> copy.getIdBook()==bookId);
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
import java.time.LocalDate;
public  class Operation{
    IBookStok bookStok = new  Stok(); //операции добавления и удаления книг на складе
    IShowBook bookShow = new  Stok(); //операции демонстрации информации о книгах
    IOrderOperation order = new Stok(); //операции с запросами и заявками

    void addBookToStock(Book book, LocalDate date){
        bookStok.addBookToStock(book, date);
    }
    void removeBookFromStock(BookCopy bookCopy){
        bookStok.removeBookFromStock(bookCopy);
    }
    String showBookInformation(Book book){
       return bookShow.showBookInformation(book);
    }
}  
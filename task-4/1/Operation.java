import java.time.LocalDate;
public  class Operation{
    StokOperation stokOperation = new  Stok();
    void addBook(Book book, LocalDate date){
        stokOperation.addBookToStock(book, date);
    }
}
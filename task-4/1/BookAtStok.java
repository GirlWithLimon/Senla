import java.time.LocalDate;
public  class BookAtStok {
    private Boolean status;
    private final Book book;
    private final LocalDate arrivalDate;

    public BookAtStok(Book book, LocalDate date) {
         this.status = true;
         this.book = book;
         this.arrivalDate = date;
    }
     public Book getBook() {
        return  this.book;
    }
     public String getStatus() {
        if(status) return  "В наличии";
        else return "Отсутствует";
    }
    public Boolean getBoolStatus() {
        return  this.status;
    }
    public LocalDate getArrivalDate() {
        return arrivalDate;
    }
    public void setStatusStok(){
        this.status = true;
        System.out.println("Добавлена на склад книга: " + this.book);
    }
    public void setStatusNo(){
        this.status = false;
    }
    @Override
    public String toString(){
        return this.book.getName();
    }
    
}
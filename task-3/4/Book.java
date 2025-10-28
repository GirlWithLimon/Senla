public class Book{
    private final String name;
    private final String author;
    private Boolean status;

    public Book(String name, String author) {
        this.name = name;
        this.author = author;
        this.status = true;
    }

    
    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        if(status) return  "В наличии";
        else return "Отсутствует";
    }
    
    public void setStatusStok(){
        this.status = true;
        System.out.println("Добавлена на склад книга: " + this);
    }
    public void setStatusNo(){
        this.status = false;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
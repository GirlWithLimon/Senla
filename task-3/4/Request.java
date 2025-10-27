public class Request{
    private final BookOrder order;

    public Request(BookOrder order) {
        this.order = order;
         System.out.println("Создан запрос на книгу: " +  this.getBook().getName());
    }
    public Book getBook(){
        return order.getBook();
    }
    public BookOrder getOrder(){
        return order;
    }
    public void ContinueRequest(){
        order.setStatus("Выполнен");
        System.out.println("Выдана книга по запросу: " + this.getBook().getName());
    }
    
}
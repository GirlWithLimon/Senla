public class Operation {
    public static void main(String[] args) {
        IBookStok shop = new Stok();
        
        Book book1 = new Book("Война и мир", "Л.Н.Толстой");
        Book book2 = new Book("Мастер и Маргарита", "М.А.Булгаков");
        shop.addBookToStock(book1);
        shop.addBookToStock(book2);
        
        shop.createOrder(book1);
               
        shop.createOrder(book1); 
         shop.createOrder(book1);
          shop.createOrder(book1);
         shop.addBookToStock(book1);
         shop.removeBookFromStock(book2);
         shop.createOrder(book2);
    }
}
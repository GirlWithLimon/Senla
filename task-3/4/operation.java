public class operation {
    public static void main(String[] args) {
        Stok shop = new Stok();
        
        // Создаем книги
        Book book1 = new Book("Война и мир", "Толстой");
        Book book2 = new Book("Преступление и наказание", "Достоевский");
        
        // Добавляем на склад
        shop.addBookToStock(book1);
        shop.addBookToStock(book2);
        
        // Создаем заказ
        shop.createOrder(book1);
        
        // Списываем книгу
        shop.removeBookFromStock(book1);
       

        // Пытаемся заказать отсутствующую книгу
        shop.createOrder(book1); // Должен создать запрос
         shop.createOrder(book1);
          shop.createOrder(book1);
         shop.addBookToStock(book1);
         shop.countRequest(book1);
    }
}
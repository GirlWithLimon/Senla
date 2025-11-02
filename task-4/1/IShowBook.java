import java.util.List;

public interface IShowBook {
    void sortByABC();
    void sortByPublicationDate();
    void sortByPrice();
    void sortByNumberCopies();
    String showBookInformation(Book book);
    void showOldBooks();
    List<BookCopy> getOldBooksSortedByDate();
    List<BookCopy> getOldBooksSortedByPrice();
}
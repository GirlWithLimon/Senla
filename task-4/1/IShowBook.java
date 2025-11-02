
public interface IShowBook{
   public void SortByABC();
   public void SortByPublicationDate();
   public void SortByPrice();
   public void SortByNumberCopies();
   public String showBookInformation(Book book);
}
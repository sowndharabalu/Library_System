package Repository;

import Model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save (Book book);
    Optional<Book> findById (int id);
    List<Book> findAll ();
    List<Book> findByAuthor (String author);
    List<Book> findByTitle (String title);
    void delete (int id);
    void updateCopies (int id, int count);
}

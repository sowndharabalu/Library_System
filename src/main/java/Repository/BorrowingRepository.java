package Repository;

import Model.Borrowing;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowingRepository {
    Borrowing save(Borrowing borrowing);

    Optional<Borrowing> findById(int id);

    List<Borrowing> findAll();

    List<Borrowing> findByUserId(int id);

    List<Borrowing> findByBookId(int id);

    List<Borrowing> findActiveBorrowings();

    List<Borrowing> findOverdueBorrowings();

    List<Borrowing> findActiveByMemberId(int id);

    long countActiveBorrowings(int id);

    void markAsReturned(int borrowingId, LocalDate returnDate);

    void delete(int id);
}

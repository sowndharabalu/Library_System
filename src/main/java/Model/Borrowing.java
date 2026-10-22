package Model;

import java.time.LocalDate;

public record Borrowing (Integer id, Integer bookId, Integer memberId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
    public Borrowing {
        if (returnDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Return Date cannot be before current date.");
        }
    }
    public static Borrowing borrowBook (Integer id, Integer bookId, Integer memberId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        if (borrowDate==null) {
            borrowDate= LocalDate.now();
        }
        if (dueDate == null) {
            dueDate = LocalDate.now().plusDays(14);
        }
        return new Borrowing (id, bookId, memberId, borrowDate, dueDate, returnDate);
    }
}

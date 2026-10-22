package Model;

import java.time.LocalDate;

public record Book (Integer id, String title, String author, String genre, Integer year, Integer availableCopies) {
    public Book {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        if (year <= 0 || year > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Year can't be 0 or later than present year");
        }
        if (availableCopies <0) {
            throw new IllegalArgumentException("AvailableCopies cannot be negative");
        }
    }
    public static Book createBook(Integer id, String title, String author, String genre, Integer year, Integer availableCopies) {
        return new Book(id, title, author, genre, year, availableCopies);
    }
}

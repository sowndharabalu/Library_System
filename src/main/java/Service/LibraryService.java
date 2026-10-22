package Service;

import Model.*;
import Repository.*;

import java.time.LocalDate;
import java.util.List;

public class LibraryService {
    private final BookRepository bookRepo;
    private final MemberRepository memberRepo;
    private final BorrowingRepository borrowingRepo;

    public LibraryService(BookRepository bookRepo, MemberRepository memberRepo, BorrowingRepository borrowingRepo) {
        this.bookRepo = bookRepo;
        this.memberRepo = memberRepo;
        this.borrowingRepo = borrowingRepo;
    }

    public Book addBook(String title, String author, String genre, int year, int copies) {
        if (title == null)
            throw new IllegalArgumentException("title is null");
        if (author == null)
            throw new IllegalArgumentException("author is null");
        if (genre == null)
            throw new IllegalArgumentException("genre is null");
        if (year <= 0)
            throw new IllegalArgumentException("year is zero");
        if (copies < 0)
            throw new IllegalArgumentException("copies is negative");
        return bookRepo.save(Book.createBook(null, title, author, genre, year, copies));
    }

    public Member addMember(String username, String email, LocalDate joinDate) {
        if (username == null)
            throw new IllegalArgumentException("username is null");
        if (email == null)
            throw new IllegalArgumentException("email is null");
        if (memberRepo.existsByEmail(email))
            throw new IllegalArgumentException("email already exists");
        return memberRepo.save(Member.createMember(null, username, email, joinDate));
    }

    public Borrowing borrowBook(int bookId, int memberId) {
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        if (book.availableCopies() <= 0)
            throw new IllegalArgumentException("Book has no available copies");
        memberRepo.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found"));
        long activeCount = borrowingRepo.findActiveByMemberId(memberId).size();
        if (activeCount >= 3)
            throw new IllegalArgumentException("Member has reached borrowing limit (3 books)");
        Borrowing borrowing = borrowingRepo.save(Borrowing.borrowBook(null, bookId, memberId, null, null, null));
        bookRepo.updateCopies(bookId, -1);
        return borrowing;
    }

    public void returnBook(int borrowingId) {
        Borrowing borrowing = borrowingRepo.findById(borrowingId).orElseThrow(() -> new IllegalArgumentException("borrowing not found"));
        if (borrowing.returnDate() != null)
            throw new IllegalArgumentException("book already returned");
        int id = borrowing.bookId();
        borrowingRepo.markAsReturned(borrowingId, LocalDate.now());
        bookRepo.findById(id).ifPresent(book -> bookRepo.updateCopies(book.id(), 1));
    }

    public List<Borrowing> getOverdueBooks() {
        return borrowingRepo.findOverdueBorrowings();
    }

    public List<Book> searchBooks(String keyword) {
        if (!bookRepo.findByAuthor(keyword).isEmpty()) {
            return bookRepo.findByAuthor(keyword);
        } else if (!bookRepo.findByTitle(keyword).isEmpty()) {
            return bookRepo.findByTitle(keyword);
        } else {
            throw new IllegalArgumentException("Can't find by keyword!");
        }
    }

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    public List<Borrowing> getMemberHistory(int memberId) {
        return borrowingRepo.findByUserId(memberId);
    }

    public List<Borrowing> getBookHistory(int bookId) {
        return borrowingRepo.findByBookId(bookId);
    }

    public List<Borrowing> getCurrentlyBorrowed() {
        return borrowingRepo.findActiveBorrowings();
    }
}
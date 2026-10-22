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
        if (title == null )
            throw new IllegalArgumentException("title is null");
        if(author == null)
            throw new IllegalArgumentException("author is null");
        if(genre == null)
            throw new IllegalArgumentException("genre is null");
        if(year <= 0)
            throw new IllegalArgumentException("year is zero");
        if(copies < 0)
            throw new IllegalArgumentException("copies is negative");
        return bookRepo.save(Book.createBook(null, title, author, genre, year, copies));
    }

    public Member addMember(String username, String email, LocalDate joinDate) {
        if(username==null){
            throw new IllegalArgumentException("username is null");
        }
        if(email==null){
            throw new IllegalArgumentException("email is null");
        }
        return memberRepo.save(Member.createMember(null, username, email, joinDate));
    }

    public Borrowing borrowBook(int bookId, int memberId) {
        if (!bookRepo.findById(bookId).isPresent() || bookRepo.findById(bookId).get().availableCopies()<=0) {
            throw new IllegalArgumentException("book not available");
        }
        Book book = bookRepo.findById(bookId).get();
        if (!memberRepo.findById(memberId).isPresent()) {
            throw new IllegalArgumentException("member not found");
        }
        bookRepo.save(Book.createBook(bookId,book.title(),book.author(),book.genre(),book.year(),book.availableCopies()-1));
        return borrowingRepo.save(Borrowing.borrowBook(null,bookId,memberId,null,null,null));
    }

    public void returnBook(int borrowingId) {
        if (!borrowingRepo.findById(borrowingId).isPresent()) {
            throw new IllegalArgumentException("borrowing not found");
        }
        Borrowing borrowing = borrowingRepo.findById(borrowingId).get();
        if (borrowing.returnDate()!=null) {
            throw new IllegalArgumentException("book already returned");
        }
        int id=borrowing.bookId();
        bookRepo.findById(id).ifPresent(book -> bookRepo.save(Book.createBook(book.id(),book.title(),book.author(),book.genre(),book.year(),book.availableCopies()+1)));
        borrowingRepo.markAsReturned(borrowingId,LocalDate.now());
    }

    public List<Borrowing> getOverdueBooks() {
        return borrowingRepo.findOverdueBorrowings();
    }

    public List<Book> searchBooks(String keyword) {
        if(!bookRepo.findByAuthor(keyword).isEmpty()){
            return bookRepo.findByAuthor(keyword);
        }else if(!bookRepo.findByTitle(keyword).isEmpty()){
            return bookRepo.findByTitle(keyword);
        }else {
            throw new IllegalArgumentException("Can't find by keyword!");
        }
    }

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }
}
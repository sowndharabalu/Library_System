import Model.*;
import Repository.*;
import Repository.book_repo.BookRepositoryImpl;
import Repository.borrow_repo.BorrowingRepositoryImpl;
import Repository.member_repo.MemberRepositoryImpl;
import Service.*;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BookRepository bookRepo = new BookRepositoryImpl();
        MemberRepository memberRepo = new MemberRepositoryImpl();
        BorrowingRepository borrowingRepo = new BorrowingRepositoryImpl();

        LibraryService service = new LibraryService(bookRepo, memberRepo, borrowingRepo);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addBook(scanner, service);
                case "2" -> listBooks(service);
                case "3" -> searchBooks(scanner, service);
                case "4" -> registerMember(scanner, service);
                case "5" -> borrowBook(scanner, service);
                case "6" -> returnBook(scanner, service);
                case "7" -> listOverdue(service);
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private static void addBook(Scanner scanner, LibraryService service) {
        System.out.println("Enter book name: ");
        String title = scanner.nextLine();
        System.out.println("Enter author: ");
        String author = scanner.nextLine();
        System.out.println("Enter genre: ");
        String genre = scanner.nextLine();
        System.out.println("Enter year: ");
        int year = scanner.nextInt();
        System.out.println("Enter copies: ");
        int copies = scanner.nextInt();
        Book book=service.addBook(title, author, genre, year, copies);
        System.out.println("Book added successfully with ID "+book.id());
    }

    private static void listBooks(LibraryService service) {
        System.out.println("There are books in the library!");
        service.getAllBooks().forEach(b->System.out.println(b.toString()));
    }

    private static void searchBooks(Scanner scanner, LibraryService service) {
        System.out.println("Enter keyword name(Title, Author): ");
        String keyword = scanner.nextLine();
        service.searchBooks(keyword).forEach(b->System.out.println(b.toString()));
    }

    private static void registerMember(Scanner scanner, LibraryService service) {
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        System.out.println("Enter email: ");
        String email = scanner.nextLine();
        Member member=service.addMember(username,email, LocalDate.now());
        System.out.println("Member added successfully with ID "+member.id());
    }

    private static void borrowBook(Scanner scanner, LibraryService service) {
        System.out.println("Enter book ID: ");
        int title = scanner.nextInt();
        System.out.println("Enter member ID: ");
        int memberID = scanner.nextInt();
        Borrowing borrowing=service.borrowBook(title, memberID);
        System.out.println("Borrowed successfully with ID "+borrowing.id());
    }

    private static void returnBook(Scanner scanner, LibraryService service) {
        System.out.println("Enter borrowing ID: ");
        int borrowingID = scanner.nextInt();
        service.returnBook(borrowingID);
        System.out.println("Returned successfully with ID "+borrowingID);
    }

    private static void listOverdue(LibraryService service) {
        System.out.println("There are books not returned yet due date");
        service.getOverdueBooks().forEach(b->System.out.println(b.toString()));
    }

    private static void printMenu() {
        System.out.println("""
                
                ═══════════ LIBRARY MANAGEMENT ═══════════
                1. Add Book
                2. List All Books
                3. Search Books
                4. Register Member
                5. Borrow Book
                6. Return Book
                7. List Overdue Books
                0. Exit
                ═══════════════════════════════════════════
                Choice:""");
    }

}


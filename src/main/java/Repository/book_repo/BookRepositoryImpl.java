package Repository.book_repo;

import DB_Connection.DatabaseConnection;
import Model.Book;
import Repository.BookRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepositoryImpl implements BookRepository {
    @Override
    public Book save(Book book) {
        if (book.id() == null) {
            return insert(book);
        } else {
            return update(book);
        }
    }

    @Override
    public Optional<Book> findById(int id) {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToBook(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find Book with id " + id);
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRowToBook(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find All Books");
        }
        return books;
    }

    @Override
    public List<Book> findByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author = ?  ";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, author);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRowToBook(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find All Books");
        }
        return books;
    }

    @Override
    public List<Book> findByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRowToBook(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find All Books");
        }
        return books;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Book has been deleted");
            } else {
                throw new RuntimeException("Can't find Book with id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find Book with id " + id);
        }
    }

    @Override
    public void updateCopies(int id, int count) {
        String sql = "UPDATE books SET available_copies  = available_copies + ? WHERE book_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, count);
            ps.setInt(2, id);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Book has been updated");
            } else {
                throw new RuntimeException("Can't find Book with id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find Book with id " + id);
        }
    }

    private Book insert(Book book) {
        String sql = "INSERT INTO books(title,author,genre,published_year,available_copies) VALUES (?,?,?,?,?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.title());
            ps.setString(2, book.author());
            ps.setString(3, book.genre());
            ps.setInt(4, book.year());
            ps.setInt(5, book.availableCopies());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int b_id = keys.getInt(1);
                return Book.createBook(b_id, book.title(), book.author(), book.genre(), book.year(), book.availableCopies());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert: " + e.getMessage());
        }
    }

    private Book update(Book book) {
        String sql = "UPDATE books SET title=?,author=?,genre=?,published_year=?,available_copies=? WHERE book_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, book.title());
            ps.setString(2, book.author());
            ps.setString(3, book.genre());
            ps.setInt(4, book.year());
            ps.setInt(5, book.availableCopies());
            ps.setInt(6, book.id());
            ps.executeUpdate();
            return book;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update: " + e.getMessage());
        }
    }

    private Book mapRowToBook(ResultSet rs) throws SQLException {

        return Book.createBook(
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("genre"),
                rs.getInt("published_year"),
                rs.getInt("available_copies")
        );
    }

}

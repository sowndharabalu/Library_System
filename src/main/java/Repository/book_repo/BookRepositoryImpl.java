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
        }else {
            return update(book);
        }
    }

    @Override
    public Optional<Book> findById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try(Connection con=DatabaseConnection.getConnection();
        PreparedStatement ps= con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1,id);
            ps.execute();
            try(ResultSet keys=ps.getGeneratedKeys()){
                keys.next();
                String title= keys.getString("title"),author= keys.getString("author"),genre= keys.getString("genre");
                int year= keys.getInt("published_year"),availableCopies= keys.getInt("available_copies");
                return Optional.of(Book.createBook(id, title, author, genre, year, availableCopies));
            }
        }catch (SQLException e){
            throw new RuntimeException("Can't find Book with id " + id);
        }
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection con=DatabaseConnection.getConnection();
        PreparedStatement ps= con.prepareStatement(sql);){
            try (ResultSet rs= ps.executeQuery()){
                while (rs.next()) {
                    int id=rs.getInt("book_id"),year=rs.getInt("published_year"),availableCopies=rs.getInt("available_copies");
                    String title=rs.getString("title"),author=rs.getString("author"),genre=rs.getString("genre");
                    books.add(Book.createBook(id, title, author, genre, year, availableCopies));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Can't find All Books");
        }
        return books;
    }

    @Override
    public List<Book> findByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author = ?  ";
        try (Connection con=DatabaseConnection.getConnection();
             PreparedStatement ps= con.prepareStatement(sql);){
            ps.setString(1,author);
            try (ResultSet rs= ps.executeQuery()){
                while (rs.next()) {
                    int id=rs.getInt("book_id"),year=rs.getInt("published_year"),availableCopies=rs.getInt("available_copies");
                    String title=rs.getString("title"),genre=rs.getString("genre");
                    books.add(Book.createBook(id, title, author, genre, year, availableCopies));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Can't find All Books");
        }
        return books;
    }

    @Override
    public List<Book> findByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title = ?";
        try (Connection con=DatabaseConnection.getConnection();
             PreparedStatement ps= con.prepareStatement(sql);){
            ps.setString(1,title);
            try (ResultSet rs= ps.executeQuery()){
                while (rs.next()) {
                    int id=rs.getInt("book_id"),year=rs.getInt("published_year"),availableCopies=rs.getInt("available_copies");
                    String author=rs.getString("author"),genre=rs.getString("genre");
                    books.add(Book.createBook(id, title, author, genre, year, availableCopies));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Can't find All Books");
        }
        return books;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try(Connection con=DatabaseConnection.getConnection();
        PreparedStatement ps= con.prepareStatement(sql)){
            int rowsDeleted=ps.executeUpdate();
            if(rowsDeleted>0){
                System.out.println("Book has been deleted");
            }else {
                throw new RuntimeException("Can't find Book with id " + id);
            }
        }catch (SQLException e){
            throw new RuntimeException("Can't find Book with id " + id);
        }
    }

    @Override
    public void updateCopies(int id, int count) {
        String sql = "UPDATE books SET availableCopies = availableCopies + ? WHERE id = ?";
        try (Connection con=DatabaseConnection.getConnection();
        PreparedStatement ps= con.prepareStatement(sql)){
            ps.setInt(1, count);
            ps.setInt(2, id);
            int rowsUpdated=ps.executeUpdate();
            if(rowsUpdated>0){
                System.out.println("Book has been updated");
            }else {
                throw new RuntimeException("Can't find Book with id " + id);
            }
        }catch (SQLException e){
            throw new RuntimeException("Can't find Book with id " + id);
        }
    }

    private Book insert(Book book) {
        String sql="INSERT INTO books(title,author,genre,published_year,available_Copies) VALUES (?,?,?,?,?)";
        try (Connection con= DatabaseConnection.getConnection();
             PreparedStatement ps= con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,book.title());
            ps.setString(2,book.author());
            ps.setString(3,book.genre());
            ps.setInt(4,book.year());
            ps.setInt(5,book.availableCopies());
            ps.executeUpdate();
            try(ResultSet keys= ps.getGeneratedKeys()){
                keys.next();
                int b_id= keys.getInt(1);
                return Book.createBook(b_id, book.title(), book.author(), book.genre(), book.year(), book.availableCopies());
            }
        }catch (SQLException e){
            throw new RuntimeException("Failed to insert: "+e.getMessage());
        }
    }

    private Book update(Book book) {
        String sql="UPDATE books SET title=?,author=?,genre=?,published_year=?,available_Copies=? WHERE id=?";
        try (Connection con=DatabaseConnection.getConnection();
        PreparedStatement ps= con.prepareStatement(sql)){
            ps.setString(1, book.title());
            ps.setString(2, book.author());
            ps.setString(3,book.genre());
            ps.setInt(4,book.year());
            ps.setInt(5,book.availableCopies());
            ps.setInt(6,book.id());
            ps.executeUpdate();
            return book;
        }catch (SQLException e){
            throw new RuntimeException("Failed to update: "+e.getMessage());
        }
    }
}

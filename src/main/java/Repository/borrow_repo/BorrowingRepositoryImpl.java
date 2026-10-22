package Repository.borrow_repo;

import DB_Connection.DatabaseConnection;
import Model.Borrowing;
import Repository.BorrowingRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BorrowingRepositoryImpl implements BorrowingRepository {
    @Override
    public Borrowing save(Borrowing borrowing) {
        if (borrowing.id() == null) {
            return insert(borrowing);
        }
        return update(borrowing);
    }

    @Override
    public List<Borrowing> findByBookId(int id) {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT * FROM borrowings WHERE book_id = ? ORDER BY borrow_date DESC";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    borrowings.add(mapRowToBorrowing(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Borrowing by Book Id", e);
        }
        return borrowings;
    }

    @Override
    public List<Borrowing> findByUserId(int id) {
        String sql = "SELECT * FROM borrowings WHERE member_id = ? ORDER BY borrow_date DESC";
        List<Borrowing> borrowings = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    borrowings.add(mapRowToBorrowing(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Borrowing by UserId", e);
        }
        return borrowings;
    }

    @Override
    public List<Borrowing> findAll() {
        String sql = "SELECT * FROM borrowings ORDER BY borrow_date DESC";
        List<Borrowing> borrowings = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                borrowings.add(mapRowToBorrowing(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find All Borrowing", e);
        }
        return borrowings;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM borrowings WHERE borrow_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete Borrowing", e);
        }
    }

    @Override
    public Optional<Borrowing> findById(int id) {
        String sql = "SELECT * FROM borrowings WHERE borrow_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToBorrowing(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Borrowing", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Borrowing> findActiveBorrowings() {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT * FROM borrowings WHERE return_date IS NULL ORDER BY due_date";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                borrowings.add(mapRowToBorrowing(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Borrowings", e);
        }
        return borrowings;
    }

    @Override
    public List<Borrowing> findOverdueBorrowings() {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT * FROM borrowings WHERE return_date IS NULL AND due_date < ? ORDER BY due_date";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setObject(1, LocalDate.now());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    borrowings.add(mapRowToBorrowing(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Borrowings", e);
        }
        return borrowings;
    }

    @Override
    public List<Borrowing> findActiveByMemberId(int id) {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT * FROM borrowings WHERE member_id = ? AND return_date IS NULL ORDER BY due_date";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    borrowings.add(mapRowToBorrowing(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Borrowings", e);
        }
        return borrowings;
    }

    @Override
    public long countActiveBorrowings(int member_id) {
        String sql = "SELECT COUNT(*) FROM borrowings WHERE member_id=? AND return_date IS NULL";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, member_id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Borrowings", e);
        }
    }

    @Override
    public void markAsReturned(int borrowingId, LocalDate returnDate) {
        String sql = "UPDATE borrowings SET return_date = ? WHERE borrow_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setObject(1, returnDate);
            ps.setInt(2, borrowingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Borrowings", e);
        }
    }

    private Borrowing insert(Borrowing borrowing) {
        String sql = "INSERT INTO borrowings(book_id,member_id,borrow_date,due_date,return_date) VALUES (?,?,?,?,?)";
        try (Connection con = DatabaseConnection.getConnection();) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, borrowing.bookId());
                ps.setInt(2, borrowing.memberId());
                ps.setObject(3, borrowing.borrowDate());
                ps.setObject(4, borrowing.dueDate());
                if (borrowing.returnDate() != null) {
                    ps.setObject(5, borrowing.returnDate());
                } else {
                    ps.setNull(5, Types.DATE);
                }
                ps.executeUpdate();
                con.commit();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    int newid = keys.getInt(1);
                    return Borrowing.borrowBook(newid, borrowing.bookId(), borrowing.memberId(), borrowing.borrowDate(), borrowing.dueDate(), borrowing.returnDate());
                }
            } catch (SQLException e) {
                con.rollback();
                throw new RuntimeException("Transaction failure", e);
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert: " + e.getMessage());
        }

    }

    private Borrowing update(Borrowing borrowing) {
        String sql = "UPDATE borrowings SET book_id = ?, member_id = ?, borrow_date = ?, due_date = ?, return_date = ? WHERE borrow_id = ?";
        try (Connection con = DatabaseConnection.getConnection();) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, borrowing.bookId());
                ps.setInt(2, borrowing.memberId());
                ps.setObject(3, borrowing.borrowDate());
                ps.setObject(4, borrowing.dueDate());
                if (borrowing.returnDate() != null) {
                    ps.setObject(5, borrowing.returnDate());
                } else {
                    ps.setNull(5, Types.DATE);
                }
                ps.setInt(6, borrowing.id());
                ps.executeUpdate();
                con.commit();
                return borrowing;
            } catch (SQLException e) {
                con.rollback();
                throw new RuntimeException("Transaction failed" + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update: " + e.getMessage());
        }
    }

    private Borrowing mapRowToBorrowing(ResultSet rs) throws SQLException {
        Date returnDateSql = rs.getDate("return_date");
        LocalDate returnDate = (returnDateSql != null) ? returnDateSql.toLocalDate() : null;
        return Borrowing.borrowBook(
                rs.getInt("borrow_id"),
                rs.getInt("book_id"),
                rs.getInt("member_id"),
                rs.getObject("borrow_date", LocalDate.class),
                rs.getObject("due_date", LocalDate.class),
                returnDate
        );
    }

}

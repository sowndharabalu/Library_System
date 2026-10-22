package Repository.member_repo;

import DB_Connection.DatabaseConnection;
import Model.Member;
import Repository.MemberRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberRepositoryImpl implements MemberRepository {
    @Override
    public Member save(Member member) {
        if (member.id() == null) {
            return insert(member);
        }
        return update(member);
    }

    @Override
    public Optional<Member> findById(int id) {
        String sql = "SELECT * FROM members WHERE member_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find member by ID");
        }
        return Optional.empty();
    }

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    members.add(mapRowToMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find members");
        }
        return members;
    }

    @Override
    public List<Member> findByName(String name) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    members.add(mapRowToMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find members");
        }
        return members;
    }

    @Override
    public Optional<Member> findByemail(String email) {
        String sql = "SELECT * FROM members WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find members");
        }
        return Optional.empty();
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM members WHERE member_id = ?";
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
    public boolean existsByEmail(String email) {
        return findByemail(email).isPresent();
    }

    private Member insert(Member member) {
        String sql = "INSERT INTO members (name, email, join_date) VALUES ( ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, member.name());
            ps.setString(2, member.email());
            ps.setObject(3, member.joinDate());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int newId = keys.getInt(1);
                return Member.createMember(newId, member.name(), member.email(), member.joinDate());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert Member", e);
        }
    }

    private Member update(Member member) {
        String sql = "UPDATE members SET name = ?, email = ?, join_date = ? WHERE member_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.name());
            ps.setString(2, member.email());
            ps.setObject(3, member.joinDate());
            ps.setInt(4, member.id());
            ps.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update Member", e);
        }
    }

    private Member mapRowToMember(ResultSet rs) throws SQLException {
        return Member.createMember(
                rs.getInt("member_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getObject("join_date",LocalDate.class)
        );
    }

}

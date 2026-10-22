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
        if(member.id() == null){
            return insert(member);
        }
        return update(member);
    }

    @Override
    public Optional<Member> findById(int id) {
        String sql = "SELECT * FROM members WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, id);
            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                keys.next();
                String name = keys.getString("name").trim(),email=keys.getString("email").trim();
                LocalDate joinDate = keys.getObject("join_date",LocalDate.class);
                return Optional.of(Member.createMember(id,name,email,joinDate));
            }
        }catch (SQLException e){
            throw new RuntimeException ("Can't find member by ID");
        }
    }

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);){
            try (ResultSet rs = ps.executeQuery()){
                rs.next();
                int id = rs.getInt("id");
                String name = rs.getString("name").trim(),email=rs.getString("email").trim();
                LocalDate joinDate = rs.getObject("join_date",LocalDate.class);
                members.add(Member.createMember(id,name,email,joinDate));
            }
        }catch (SQLException e){
            throw new RuntimeException ("Can't find members");
        }
        return members;
    }

    @Override
    public List<Member> findByName(String name) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE name = ?";
        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    int id = rs.getInt("id");
                    String email = rs.getString("email").trim();
                    LocalDate joinDate = rs.getObject("join_date",LocalDate.class);
                    members.add(Member.createMember(id,name,email,joinDate));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException ("Can't find members");
        }
        return members;
    }

    @Override
    public Optional<Member> findByemail(String email) {
        String sql = "SELECT * FROM members WHERE email = ?";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()){
                rs.next();
                int id = rs.getInt("id");
                String name = rs.getString("name").trim();
                LocalDate joinDate = rs.getObject("join_date",LocalDate.class);
                return Optional.of(Member.createMember(id,name,email,joinDate));
            }
        }catch (SQLException e){
            throw new RuntimeException ("Can't find members");
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM members WHERE id = ?";
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
    public boolean existsByEmail(String email) {
        return findByemail(email).isPresent();
    }

    private Member insert(Member member) {
        String sql = "INSERT INTO Members (name, email, join_date) VALUES ( ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, member.name());
            ps.setString(2, member.email());
            if (member.joinDate() != LocalDate.now())
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
        String sql="INSERT INTO Members (name, email, join_date) VALUES (?, ?, ?) WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.name());
            ps.setString(2, member.email());
            ps.setObject(3, member.joinDate());
            ps.executeUpdate();
            return member;
        }catch (SQLException e){
            throw new RuntimeException("Failed to update Member", e);
        }
    }
}

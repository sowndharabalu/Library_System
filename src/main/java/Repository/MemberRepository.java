package Repository;

import Model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save (Member Member);
    Optional<Member> findByemail (String email);
    Optional<Member> findById (int id);
    List<Member> findAll ();
    List<Member> findByName (String name);
    void delete (int id);
    boolean existsByEmail (String email);
}

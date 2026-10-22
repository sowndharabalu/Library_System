package Model;

import java.time.LocalDate;

public record Member (Integer id, String name, String email, LocalDate joinDate) {
    public Member {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        if (joinDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Join date cannot be after now");
        }
    }
    public static Member createMember (Integer id, String name, String email, LocalDate joinDate){
        if (joinDate == null) {
            return new Member(id, name, email, LocalDate.now());
        }
        return new Member(id, name, email, joinDate);
    }
}

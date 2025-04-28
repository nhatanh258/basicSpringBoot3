package practiceAPIspring.managingUsers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import practiceAPIspring.managingUsers.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {
    @Query(value = "SELECT * FROM user WHERE full_name = :name", nativeQuery = true)
    List<User> findByName(@Param("name") String name);

    @Query(value = "SELECT * FROM user WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);
}

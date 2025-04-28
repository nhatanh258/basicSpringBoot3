package practiceAPIspring.managingUsers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import practiceAPIspring.managingUsers.model.Role;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role ,String> {
    @Query(value = "SELECT * FROM role WHERE name = :name LIMIT 1", nativeQuery = true)
    Optional<Role> findByName(@Param("name") String name);

}

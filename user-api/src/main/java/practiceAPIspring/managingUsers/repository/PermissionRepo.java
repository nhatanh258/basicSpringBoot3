package practiceAPIspring.managingUsers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import practiceAPIspring.managingUsers.model.Permission;


import java.util.Optional;


public interface PermissionRepo extends JpaRepository<Permission,String> {
    @Query(value = "SELECT * FROM permission WHERE role_access = :name", nativeQuery = true)
    Optional<Permission> findByName(String name);
}

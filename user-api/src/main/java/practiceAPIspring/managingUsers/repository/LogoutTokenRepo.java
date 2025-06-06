package practiceAPIspring.managingUsers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practiceAPIspring.managingUsers.model.logoutToken;


public interface LogoutTokenRepo extends JpaRepository<logoutToken,String> {
}

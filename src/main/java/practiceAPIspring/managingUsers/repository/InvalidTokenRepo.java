package practiceAPIspring.managingUsers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practiceAPIspring.managingUsers.model.InvalidToken;


public interface InvalidTokenRepo extends JpaRepository<InvalidToken,String> {
}

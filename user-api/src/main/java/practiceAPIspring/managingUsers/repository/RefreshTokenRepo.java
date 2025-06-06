package practiceAPIspring.managingUsers.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import practiceAPIspring.managingUsers.model.RefreashToken;


public interface RefreshTokenRepo extends JpaRepository<RefreashToken,String> {
}

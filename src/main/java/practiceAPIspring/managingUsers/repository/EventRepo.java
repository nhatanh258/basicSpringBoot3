package practiceAPIspring.managingUsers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practiceAPIspring.managingUsers.model.Event;


public interface EventRepo extends JpaRepository<Event,String> {
}

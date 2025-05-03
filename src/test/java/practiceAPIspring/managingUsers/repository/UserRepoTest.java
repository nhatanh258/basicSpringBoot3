package practiceAPIspring.managingUsers.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import practiceAPIspring.managingUsers.model.User;

@SpringBootTest// su dung cau hinh yml voi database that
@Transactional  //Tự động rollback sau mỗi test. ==> khong save that
//@Rollback(false) // <<< Thêm dòng này để không rollback
class UserRepoTest {

    @Autowired
    private UserRepo userRepo;

    @Test
    void testFindByName() {
        // Given
        User user = new User();
        user.setId("u1");
        user.setFullName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("123456");
        userRepo.save(user);

        // When
        List<User> result = userRepo.findByName("Alice");

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getFullName()).isEqualTo("Alice");//Kiểm tra dữ liệu trả về đúng như mong đợi.
    }

    @Test
    void testFindByEmail() {
        // Given
        User user = new User();
        user.setId("u2");
        user.setFullName("Bob");
        user.setEmail("bob@example.com");
        user.setPassword("1234567");
        userRepo.save(user);

        // When
        Optional<User> result = userRepo.findByEmail("bob@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("bob@example.com");
    }
}

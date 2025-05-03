package practiceAPIspring.managingUsers.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import practiceAPIspring.managingUsers.model.Role;

import java.util.Optional;

@SpringBootTest // Sử dụng cấu hình thật (application.yml)
@Transactional    // Tự rollback sau mỗi test
//@Rollback(false)  // Bỏ rollback nếu muốn lưu dữ liệu vào database
class RoleRepoTest {

    @Autowired
    private RoleRepo roleRepo;

    @Test
    void testFindByName() {
        // Given
        Role role = new Role();
        role.setId("r1");
        role.setName("ADMIN");
        roleRepo.save(role);

        // When
        Optional<Role> result = roleRepo.findByName("ADMIN");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("ADMIN");
    }

    @Test
    void testFindByName_NotFound() {
        // When
        Optional<Role> result = roleRepo.findByName("UNKNOWN");

        // Then
        assertThat(result).isNotPresent();
    }
}

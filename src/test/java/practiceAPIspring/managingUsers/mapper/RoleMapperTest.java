package practiceAPIspring.managingUsers.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practiceAPIspring.managingUsers.dto.request.role.RoleRequest;
import practiceAPIspring.managingUsers.dto.response.role.RoleResponse;
import practiceAPIspring.managingUsers.model.Role;

@SpringBootTest
class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;

    @Test
    void testToRole() {
        RoleRequest request = new RoleRequest();
        request.setName("Admin");
        request.setDescription("Administrator role");

        Role role = roleMapper.toRole(request);

        assertThat(role).isNotNull();
        assertThat(role.getName()).isEqualTo("Admin");
        assertThat(role.getDescription()).isEqualTo("Administrator role");
        assertThat(role.getPermissions()).isNull(); // vì được ignore
    }

    @Test
    void testToRoleResponse() {
        Role role = new Role();
        role.setName("User");
        role.setDescription("Regular user");
        // Giả sử bạn set permissions ở đây nếu cần

        RoleResponse response = roleMapper.toRoleResponse(role);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("User");
        assertThat(response.getDescription()).isEqualTo("Regular user");
        // assertThat(response.getPermissions()) nếu bạn có set permissions
    }
}

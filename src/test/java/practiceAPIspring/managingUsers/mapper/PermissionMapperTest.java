package practiceAPIspring.managingUsers.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practiceAPIspring.managingUsers.dto.request.permission.PermissionRequest;
import practiceAPIspring.managingUsers.dto.response.permission.PermissionResponse;
import practiceAPIspring.managingUsers.model.Permission;

@SpringBootTest
class PermissionMapperTest {

    @Autowired
    private PermissionMapper permissionMapper;

    @Test
    void testToPermission() {
        PermissionRequest request = new PermissionRequest();
        request.setRole("ADMIN");
        request.setDescription("Access to admin features");

        Permission permission = permissionMapper.toPermission(request);

        assertThat(permission).isNotNull();
        assertThat(permission.getRole()).isEqualTo("ADMIN");
        assertThat(permission.getDescription()).isEqualTo("Access to admin features");
    }

    @Test
    void testToPermissionResponse() {
        Permission permission = new Permission();
        permission.setRole("USER");
        permission.setDescription("Can view profile");

        PermissionResponse response = permissionMapper.toPermissionResponse(permission);

        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo("USER");
        assertThat(response.getDescription()).isEqualTo("Can view profile");
    }
}

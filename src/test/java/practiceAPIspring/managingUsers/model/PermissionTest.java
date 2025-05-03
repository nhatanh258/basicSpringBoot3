package practiceAPIspring.managingUsers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PermissionTest {

    @InjectMocks
    private Permission permission;

    @Mock
    private List<Role> roles;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        permission = new Permission("uuid-5678", "READ_PRIVILEGES", "Permission to read data", roles);
    }

    @Test
    void testPermissionCreation() {
        assertNotNull(permission);
        assertEquals("uuid-5678", permission.getId());
        assertEquals("READ_PRIVILEGES", permission.getRole());
        assertEquals("Permission to read data", permission.getDescription());
    }

    @Test
    void testPermissionRoles() {
        List<Role> roles = mock(List.class);
        permission.setRoles(roles);
        assertEquals(roles, permission.getRoles());
    }

    @Test
    void testPermissionRole() {
        permission.setRole("WRITE_PRIVILEGES");
        assertEquals("WRITE_PRIVILEGES", permission.getRole());
    }

    @Test
    void testPermissionDescription() {
        permission.setDescription("Permission to write data");
        assertEquals("Permission to write data", permission.getDescription());
    }

}

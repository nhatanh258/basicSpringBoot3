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
class RoleTest {

    @InjectMocks
    private Role role;

    @Mock
    private Set<Permission> permissions;

    @Mock
    private List<User> users;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        role = new Role("uuid-1234", "ADMIN", "Admin role", permissions, users);
    }

    @Test
    void testRoleCreation() {
        assertNotNull(role);
        assertEquals("uuid-1234", role.getId());
        assertEquals("ADMIN", role.getName());
        assertEquals("Admin role", role.getDescription());
    }

    @Test
    void testRolePermissions() {
        Set<Permission> permissions = mock(Set.class);
        role.setPermissions(permissions);
        assertEquals(permissions, role.getPermissions());
    }

    @Test
    void testRoleUsers() {
        List<User> users = mock(List.class);
        role.setUsers(users);
        assertEquals(users, role.getUsers());
    }

    @Test
    void testRoleName() {
        role.setName("USER");
        assertEquals("USER", role.getName());
    }

    @Test
    void testRoleDescription() {
        role.setDescription("User role");
        assertEquals("User role", role.getDescription());
    }

    @Test
    void testToString() {
        String expectedToString = "Role(id=uuid-1234, name=ADMIN, description=Admin role)";
        assertEquals(expectedToString, role.toString());
    }

}

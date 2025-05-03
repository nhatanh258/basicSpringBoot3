package practiceAPIspring.managingUsers.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import practiceAPIspring.managingUsers.dto.response.role.RoleResponse;
import practiceAPIspring.managingUsers.dto.response.user.UserResponse;
import practiceAPIspring.managingUsers.model.Permission;
import practiceAPIspring.managingUsers.model.Role;
import practiceAPIspring.managingUsers.model.User;

import java.util.*;
import java.util.stream.Collectors;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserMapperTest {

    private Permission permission;

    @InjectMocks
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock dữ liệu cho User
        user = new User();
        user.setId("123");
        user.setFullName("John Doe");
        user.setNumberId("12345");
        user.setPhoneNumber("123-456-789");
        user.setEmail("john.doe@example.com");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());


        //permision
        permission = Permission.builder()
                .id(UUID.randomUUID().toString())//Test (mocking, không có Hibernate) – không có ai sinh UUID giúp bạn nếu bạn không làm thủ công
                .role("ADMIN_Approve")
                .description("Approve user data")
                .build();

        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

        // Mock dữ liệu cho roles
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName("Admin");
        role.setDescription("Administrator role");
        role.setPermissions(permissions);
        role.setId(UUID.randomUUID().toString());

        roles.add(role);
        user.setRoles(roles);


    }

    @Test
    void testToUserResponse() {
        List<RoleResponse> roleResponses = user.getRoles().stream()
                .map(role -> RoleResponse
                        .builder()
                        .name(role.getName())
                        .description(role.getDescription())
                        .permissions(role.getPermissions())
                        .build())
                .collect(Collectors.toList());

        when(roleMapper.toRoleResponse(any(Role.class)))
                .thenReturn(roleResponses.get(0),
                        roleResponses.subList(1, roleResponses.size()).toArray(new RoleResponse[0]));


        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setNumberId(user.getNumberId());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmail(user.getEmail());
        response.setRoles(
                user.getRoles()
                        .stream()
                        .map(s -> roleMapper.toRoleResponse(s)) // return rõ ràng
                        .collect(Collectors.toSet())
        );

        List<RoleResponse> roleResponses2 = user.getRoles().stream()
                .map(role -> RoleResponse
                        .builder()
                        .name(role.getName())
                        .description(role.getDescription())
                        .permissions(role.getPermissions())
                        .build())
                .collect(Collectors.toList());

        when(roleMapper.toRoleResponse(any(Role.class)))
                .thenReturn(roleResponses2.get(0),
                        roleResponses2.subList(1, roleResponses2.size()).toArray(new RoleResponse[0]));

        // Call the method to test
        UserResponse userResponse = userMapper.toUserResponse(user);

        // Assertions
        assertNotNull(userResponse);
        assertEquals("123", userResponse.getId());
        assertEquals("John Doe", userResponse.getFullName());
        assertEquals("12345", userResponse.getNumberId());
        assertEquals("123-456-789", userResponse.getPhoneNumber());
        assertEquals("john.doe@example.com", userResponse.getEmail());
        assertNotNull(userResponse.getRoles());
        assertEquals(1, userResponse.getRoles().size());
        assertEquals("Admin", userResponse.getRoles().iterator().next().getName());
        assertEquals("Administrator role", userResponse.getRoles().iterator().next().getDescription());

        // Verify that roleMapper's toRoleResponse was called exactly once
        verify(roleMapper, times(2)).toRoleResponse(any(Role.class));
    }

    @Test
    void testToUserResponseWithEmptyRoles() {
        user.setRoles(new HashSet<>());  // Set roles to empty set

        // Call the method to test
        UserResponse userResponse = userMapper.toUserResponse(user);

        // Assertions
        assertNotNull(userResponse);
        assertEquals("123", userResponse.getId());
        assertTrue(userResponse.getRoles().isEmpty(), "Roles should be empty");
    }
}

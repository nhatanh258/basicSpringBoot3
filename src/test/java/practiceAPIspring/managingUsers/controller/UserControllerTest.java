package practiceAPIspring.managingUsers.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import practiceAPIspring.managingUsers.dto.request.User.UserStoreRequest;
import practiceAPIspring.managingUsers.dto.response.user.UserResponse;
import practiceAPIspring.managingUsers.mapper.RoleMapper;
import practiceAPIspring.managingUsers.model.Permission;
import practiceAPIspring.managingUsers.model.Role;
import practiceAPIspring.managingUsers.model.User;
import practiceAPIspring.managingUsers.repository.UserRepo;
import practiceAPIspring.managingUsers.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    private UserStoreRequest request;
    private Permission permission;
    private Role role;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private RoleMapper roleMapper;

    private ObjectMapper objectMapper;

    private String tokenOfAdmin;
    private String tokenComon;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        objectMapper = new ObjectMapper();
        userRepository.deleteAll();

        // Tạo user giả lập (admin) lưu vào database
        User user = new User();
        user.setId("123");
        user.setEmail("admin@gmail.com");
        user.setPassword("secret"); // giả sử đã được mã hóa
        user.setFullName("Admin User");
        permission = Permission.builder()
                .id(UUID.randomUUID().toString())//Test (mocking, không có Hibernate) – không có ai sinh UUID giúp bạn nếu bạn không làm thủ công
                .role("APPROVE_POST")
                .description("Read user data")
                .build();

        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

        role = Role.builder()
                .id(UUID.randomUUID().toString())
                .name("ADMIN")
                .description("ADMIN role")
                .permissions(permissions)// vi role la ben chu dong tao bang nen no can set permission
                .build();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles); // hoặc dùng List<Role>

        userRepository.save(user);
    }

    @Test
    void testGetList() throws Exception {
        Mockito.when(userService.getAll()).thenReturn(List.of(new UserResponse()));

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + tokenOfAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SUCCESS"))  //  Đổi từ $.status thành $.message
                .andExpect(jsonPath("$.data").isArray());           //  Kiểm tra data là mảng
    }



    @Test
    void testGetUserById() throws Exception {
        tokenComon= "lay token tu login normal";
        mockMvc.perform(get("/api/user/123")
                .header("Authorization", "Bearer " + tokenComon))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SUCCESS"))  //  Đổi từ $.status thành $.message
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testRegisterUser() throws Exception {
        request = new UserStoreRequest();
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setPassword("123456");
        request.setPhoneNumber("0123456789");
        request.setNumberId("9999");
        //set up moi quan he role va permission
        permission = Permission.builder()
//                .id(UUID.randomUUID().toString())//Test (mocking, không có Hibernate) – không có ai sinh UUID giúp bạn nếu bạn không làm thủ công
                .role("USER_READ")
                .description("Read user data")
                .build();

        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

        role = Role.builder()
//                .id(UUID.randomUUID().toString())
                .name("USER")
                .description("User role")
                .permissions(permissions)// vi role la ben chu dong tao bang nen no can set permission
                .build();


        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}

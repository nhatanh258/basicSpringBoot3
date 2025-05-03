package practiceAPIspring.managingUsers.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import practiceAPIspring.managingUsers.constant.StatusMessage;
import practiceAPIspring.managingUsers.dto.request.User.UserRequest;
import practiceAPIspring.managingUsers.dto.request.User.UserStoreRequest;
import practiceAPIspring.managingUsers.dto.response.role.RoleResponse;
import practiceAPIspring.managingUsers.dto.response.user.UserResponse;
import practiceAPIspring.managingUsers.mapper.RoleMapper;
import practiceAPIspring.managingUsers.mapper.UserMapper;
import practiceAPIspring.managingUsers.model.Permission;
import practiceAPIspring.managingUsers.model.Role;
import practiceAPIspring.managingUsers.model.User;
import practiceAPIspring.managingUsers.repository.RoleRepo;
import practiceAPIspring.managingUsers.repository.UserRepo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Trong Unit Test, bạn không cần viết lại các annotation như
//        @Service, @AllArgsConstructor, @Repository của class UserService.
//Những annotation đó chỉ có tác dụng lúc runtime khi Spring Boot khởi chạy, còn khi viết test

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    RoleResponse RoleResponse;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Role role;

    private RoleMapper roleMapper;

    @Mock
    private Permission permission;
            ;
    @InjectMocks
    private UserService userService;// day la doi tuong that

    private UserStoreRequest request;

    @Mock
    private UserMapper userMapper;

    private UserResponse userResponse;


// khoi tao mot dioo tuong chung cho moi test
    @BeforeEach
    void setup() {
        roleMapper = Mappers.getMapper(RoleMapper.class);
        request = new UserStoreRequest();
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setPassword("123456");
        request.setPhoneNumber("0123456789");
        request.setNumberId("9999");
        //set up moi quan he role va permission
        permission = Permission.builder()
                .id(UUID.randomUUID().toString())//Test (mocking, không có Hibernate) – không có ai sinh UUID giúp bạn nếu bạn không làm thủ công
                .role("USER_READ")
                .description("Read user data")
                .build();

        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

         role = Role.builder()
                .id(UUID.randomUUID().toString())
                .name("USER")
                .description("User role")
                .permissions(permissions)// vi role la ben chu dong tao bang nen no can set permission
                .build();

    }

    @Test
    void testStore_shouldSaveUserWithEncodedPasswordAndRole() {
        // Given
        when(roleRepo.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenAnswer(repoMock ->
                repoMock.getArguments()[0]);//khong save real

        // When
        User result = userService.store(request);

        // Then
        assertNotNull(result);
        assertEquals("Test User", result.getFullName());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getRoles().contains(role));
    }
    @Test
    void testStore_shouldThrowExceptionWhenRoleNotFound() {
        // Given
        UserStoreRequest request = new UserStoreRequest();
        request.setEmail("test@example.com");
        request.setPassword("123456");
        request.setFullName("Test User");
        request.setNumberId("123456789");
        request.setPhoneNumber("0987654321");

        // Giả lập roleRepo không tìm thấy role
        when(roleRepo.findByName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.store(request);
        });

        // Kiểm tra nội dung lỗi trả về đúng mong đợi
        assertTrue(exception.getMessage().contains("Role USER not found"));
    }

    @Test
    void testGetAll_shouldReturnMappedUserResponses() {
        // Givenc&& when
        when(roleRepo.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        User user1 = User.builder()
                .fullName(request.getFullName())
                .numberId(request.getNumberId())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        Role role = roleRepo.findByName("USER")// tra ve 1 role
                .orElseThrow(() -> new RuntimeException("Role USER not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user1.setRoles(roles);

        when(userRepo.save(any())).thenReturn(user1);
        user1 = userService.store(request);

        when(passwordEncoder.encode("12345")).thenReturn("encodedPasswordd");
        UserStoreRequest request2 = new UserStoreRequest();
        request2.setEmail("test2@example.com");
        request2.setFullName("Test2 User");
        request2.setPassword("12345");
        request2.setPhoneNumber("0123456786");
        request2.setNumberId("9998");
        User user2 = User.builder()
                .fullName(request2.getFullName())
                .numberId(request2.getNumberId())
                .phoneNumber(request2.getPhoneNumber())
                .email(request2.getEmail())
                .password(passwordEncoder.encode(request2.getPassword()))
                .build();

        Set<Role> roles2 = new HashSet<>();
        roles2.add(role);
        user2.setRoles(roles2);
        when(userRepo.save(any())).thenReturn(user2);
         user2 = userService.store(request2);


        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        when(userRepo.findAll()).thenReturn(userList);

        // truyen cac tham so mock vao
        UserService serviceWithRealMapper = new UserService(roleMapper, passwordEncoder, userRepo, roleRepo);

        List<UserResponse> result = serviceWithRealMapper.getAll();

        // Then
        assertEquals(2, result.size());
    }
    @Test
    void testGetAll_shouldThrowRuntimeException_whenFindAllFails() {
        // Given
        when(userRepo.findAll()).thenThrow(new RuntimeException("Database down"));

        UserService service = new UserService(roleMapper, passwordEncoder, userRepo, roleRepo);

        // When + Then
        RuntimeException exception = assertThrows(RuntimeException.class, service::getAll);
        assertEquals("Error get all users: Database down", exception.getMessage());
    }


    @Test
    void testGet_shouldReturnUserIfExists() {
        // Given
        String id = "user-id";
        User user = User.builder()
                .fullName("Someone")
                .build();
        user.setId(id);
        Set<Role> roles2 = new HashSet<>();
        roles2.add(role);
        user.setRoles(roles2);

        UserService userService1 = new UserService(roleMapper, passwordEncoder, userRepo, roleRepo);
        when(userRepo.findById(id)).thenReturn(Optional.of(user));


        // When
        Optional<?> response = userService1.get(id);

        // Then
        assertTrue(response.isPresent());
        assertEquals(true, response.get().toString().contains("Someone"));
    }
    @Test
    void testGet_shouldThrowExceptionWhenUserNotFound() {
        // Given
        String userId = "non-existent-id";

        // Giả lập userRepo không tìm thấy user
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.get(userId);
        });

        // Kiểm tra nội dung lỗi trả về đúng mong đợi
        assertTrue(exception.getMessage().contains("User not found with id: " + userId));
    }


    @Test
    void testGetMyInfor_shouldReturnUser() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            // Given
            SecurityContext context = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(roleRepo.findByName("USER")).thenReturn(Optional.of(role));
            when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
            when(userRepo.save(any(User.class))).thenAnswer(repoMock ->
                    repoMock.getArguments()[0]);//khong save real

            // When
            User result = userService.store(request);

            when(SecurityContextHolder.getContext()).thenReturn(context);
            when(context.getAuthentication()).thenReturn(authentication); // 👈 THIẾT YẾU
            when(authentication.getName()).thenReturn(result.getEmail());

            when(this.userRepo.findByEmail(result.getEmail())).thenReturn(Optional.of(result));

            UserService userService1 = new UserService(roleMapper, passwordEncoder, userRepo, roleRepo);

            User newUser = userService1.getMyInfor();

            //then
            assertEquals(newUser.getFullName(), result.getFullName());
        }

    }

    @Test
    void testGetMyInfor_shouldThrowExceptions(){
        String email = "nonexistent@example.com";
        Authentication authentication = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            // Setup mock SecurityContextHolder
            when(SecurityContextHolder.getContext()).thenReturn(context);
            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(email);

            // Giả lập user không tồn tại
            when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            ApplicationContextException exception = assertThrows(
                    ApplicationContextException.class,
                    () -> userService.getMyInfor()
            );

            assertEquals(StatusMessage.NOT_FOUND, exception.getMessage());

        }
    }

    @Test
    void destroy_shouldReturnUser_whenFoundAndDeletedSuccessfully() {
        // Given
        String userId = "123";
        User user = new User();
        user.setId(userId);
        user.setFullName("Test User");

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // When
        User result = userService.destroy(userId);

        // Then
        assertNotNull(result);
        assertEquals("Test User", result.getFullName());
        verify(userRepo).deleteById(userId);//hương thức deleteById(userId) của mock userRepo đã được gọi đúng một lần với tham số là userId.
    }

    @Test
    void destroy_shouldThrowException_whenUserNotFound() {
        // Given
        String userId = "not_exist";

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        IllegalCallerException exception = assertThrows(
                IllegalCallerException.class,
                () -> userService.destroy(userId)
        );

        assertEquals("xoa that bai", exception.getMessage());
        verify(userRepo, never()).deleteById(any());
    }




}

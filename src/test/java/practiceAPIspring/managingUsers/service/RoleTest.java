package practiceAPIspring.managingUsers.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import practiceAPIspring.managingUsers.dto.request.User.UserStoreRequest;
import practiceAPIspring.managingUsers.dto.request.role.RoleRequest;
import practiceAPIspring.managingUsers.dto.response.role.RoleResponse;
import practiceAPIspring.managingUsers.mapper.RoleMapper;
import practiceAPIspring.managingUsers.model.Permission;
import practiceAPIspring.managingUsers.model.Role;
import practiceAPIspring.managingUsers.repository.PermissionRepo;
import practiceAPIspring.managingUsers.repository.RoleRepo;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleTest {

    private Permission permission;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private PermissionRepo permissionRepo;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;
    @BeforeEach
    void setup() {

    }
    @Test
    void testCreate_shouldReturnRoleResponse() {
        String permName = "ADMIN";
        String permId = UUID.randomUUID().toString();
        String discrip = "APPROVED_POST";

        Permission permission = new Permission();
        RoleRequest roleRequest = mock(RoleRequest.class);
        permission.setId(permId);
        permission.setRole(permName);
        permission.setDescription(discrip);


        Set<String> permissionIds = Set.of(permission.getId());
        Role newRole = new Role();
        newRole.setId(UUID.randomUUID().toString());
        newRole.setName(permName);
        newRole.setDescription(discrip);
        Set<Permission> permissionSet = new HashSet<>();
        permissionSet.add(permission);
        newRole.setPermissions(permissionSet);


        RoleResponse response = new RoleResponse();
       response.setName(newRole.getName());
       response.setDescription(newRole.getDescription());
       response.setPermissions(permissionSet);

        when(roleMapper.toRole(roleRequest)).thenReturn(newRole);

        when(roleRequest.getTransferStrings(permissionRepo))
                .thenReturn(permissionIds);
//        when(permissionRepo.findByName(permission.getRole())).thenReturn(Optional.of(permission));

        when(permissionRepo.findAllById(permissionIds)).thenReturn(new ArrayList<>(permissionSet));


        when(roleRepo.save(newRole)).thenReturn(newRole);
        when(roleMapper.toRoleResponse(newRole)).thenReturn(response);

        RoleResponse result = roleService.create(roleRequest);

        assertEquals(response, result);
        assertEquals(response.getName(), result.getName());
        assertEquals(response.getDescription(), result.getDescription());
        assertEquals(response.getPermissions(), result.getPermissions());
    }

    @Test
    void testCreate_shouldThrowException_whenPermissionRepoFails() {
        RoleRequest request = mock(RoleRequest.class);
        Role role = new Role();

        when(roleMapper.toRole(request)).thenReturn(role);
        when(request.getTransferStrings(permissionRepo)).thenThrow(new RuntimeException("Permission fetch failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> roleService.create(request));
        assertEquals("Permission fetch failed", ex.getMessage());
    }

    @Test
    void testGetAll_shouldReturnMappedRoleResponses() {
        Role role = new Role();
        RoleResponse roleResponse = new RoleResponse();

        when(roleRepo.findAll()).thenReturn(List.of(role));
        when(roleMapper.toRoleResponse(role)).thenReturn(roleResponse);

        List<RoleResponse> result = roleService.getAll();

        assertEquals(1, result.size());
        assertEquals(roleResponse, result.get(0));
    }

    @Test
    void testDelete_shouldDeleteIfExists() {
        Role role = new Role();
        when(roleRepo.findById("roleId")).thenReturn(Optional.of(role));

        roleService.delete("roleId");

        verify(roleRepo).delete(role);
    }

    @Test
    void testDelete_shouldThrowIfNotExists() {
        when(roleRepo.findById("invalidId")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> roleService.delete("invalidId"));

        assertEquals("Role 'invalidId' không tồn tại, không thể xóa.", ex.getMessage());
    }
}

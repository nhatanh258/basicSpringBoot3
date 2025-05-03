package practiceAPIspring.managingUsers.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import practiceAPIspring.managingUsers.dto.request.permission.PermissionRequest;
import practiceAPIspring.managingUsers.dto.response.permission.PermissionResponse;
import practiceAPIspring.managingUsers.mapper.PermissionMapper;
import practiceAPIspring.managingUsers.model.Permission;
import practiceAPIspring.managingUsers.repository.PermissionRepo;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionTest {

    @Mock
    private PermissionRepo permissionRepo;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionService permissionService;

    private PermissionRequest permissionRequest;
    private Permission permission;
    private PermissionResponse permissionResponse;

    @BeforeEach
    void setUp() {

        permissionRequest = new PermissionRequest();
        permissionRequest.setRole("ADMIN");
        permissionRequest.setDescription("Can approve posts");

        permission = Permission.builder()
                .id(UUID.randomUUID().toString())
                .role("ADMIN")
                .description("Can approve posts")
                .build();

        permissionResponse = new PermissionResponse();
        permissionResponse.setRole(permission.getRole());
        permissionResponse.setDescription(permission.getDescription());
    }

    @Test
    void testCreatePermission() {
        when(permissionMapper.toPermission(permissionRequest)).thenReturn(permission);
        when(permissionRepo.save(permission)).thenReturn(permission);
        when(permissionMapper.toPermissionResponse(permission)).thenReturn(permissionResponse);

        PermissionResponse result = permissionService.create(permissionRequest);

        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        verify(permissionRepo).save(permission);
        verify(permissionMapper).toPermission(permissionRequest);
        verify(permissionMapper).toPermissionResponse(permission);
    }

    @Test
    void testGetAllPermissions() {
        List<Permission> permissions = List.of(permission);
        when(permissionRepo.findAll()).thenReturn(permissions);
        when(permissionMapper.toPermissionResponse(permission)).thenReturn(permissionResponse);

        List<PermissionResponse> result = permissionService.getAll();

        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getRole());
        verify(permissionRepo).findAll();
    }

    @Test
    void testDeletePermission() {
        String id = permission.getId();

        doNothing().when(permissionRepo).deleteById(id);

        permissionService.delete(id);

        verify(permissionRepo).deleteById(id);
    }
}

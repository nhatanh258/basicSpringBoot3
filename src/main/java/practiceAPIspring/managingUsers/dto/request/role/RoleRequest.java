package practiceAPIspring.managingUsers.dto.request.role;

import lombok.*;
import practiceAPIspring.managingUsers.model.Permission;
import practiceAPIspring.managingUsers.repository.PermissionRepo;

import java.util.Set;
import java.util.stream.Collectors;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class RoleRequest {
    private String name;
    private  String description;
    private Set<String> permissions;

    public Set<String> getTransferStrings(PermissionRepo permissionRepository) {
        // Chuyển String sang UUID (ví dụ)
        return permissions.stream()
                .map(permissionName -> permissionRepository.findByName(permissionName)
                        .map(Permission::getId)
                        .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName)))
                .collect(Collectors.toSet());
    }
}

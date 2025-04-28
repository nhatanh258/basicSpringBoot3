package practiceAPIspring.managingUsers.dto.response.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import practiceAPIspring.managingUsers.dto.response.permission.PermissionResponse;
import practiceAPIspring.managingUsers.model.Permission;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RoleResponse {
    private String name;
    private  String description;
    private Set<Permission> permissions;
}


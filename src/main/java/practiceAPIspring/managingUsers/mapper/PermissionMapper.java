package practiceAPIspring.managingUsers.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import practiceAPIspring.managingUsers.dto.request.permission.PermissionRequest;
import practiceAPIspring.managingUsers.dto.response.permission.PermissionResponse;
import practiceAPIspring.managingUsers.model.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @Mapping(source = "role", target = "role")
    @Mapping(source = "description", target = "description")
    Permission toPermission(PermissionRequest request);

    @Mapping(source = "role", target = "role")
    @Mapping(source = "description", target = "description")
    PermissionResponse toPermissionResponse(Permission permission);
}

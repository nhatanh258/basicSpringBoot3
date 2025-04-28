package practiceAPIspring.managingUsers.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import practiceAPIspring.managingUsers.dto.request.role.RoleRequest;
import practiceAPIspring.managingUsers.dto.response.role.RoleResponse;
import practiceAPIspring.managingUsers.model.Role;




@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "permissions", target = "permissions")
    RoleResponse toRoleResponse(Role role);
}

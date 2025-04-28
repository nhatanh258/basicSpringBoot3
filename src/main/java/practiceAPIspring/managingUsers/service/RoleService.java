package practiceAPIspring.managingUsers.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import practiceAPIspring.managingUsers.dto.request.role.RoleRequest;
import practiceAPIspring.managingUsers.dto.response.role.RoleResponse;
import practiceAPIspring.managingUsers.mapper.RoleMapper;
import practiceAPIspring.managingUsers.model.Permission;
import practiceAPIspring.managingUsers.model.Role;
import practiceAPIspring.managingUsers.repository.PermissionRepo;
import practiceAPIspring.managingUsers.repository.RoleRepo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Service
@AllArgsConstructor

@Slf4j

public class RoleService {
    private final RoleRepo roleRepo;
    PermissionRepo permissionRepository;
    private RoleMapper roleMapper;

    public RoleResponse create(RoleRequest roleRequest){
        var role = roleMapper.toRole(roleRequest);
        var permissions = permissionRepository.findAllById(roleRequest.getTransferStrings(permissionRepository));
        role.setPermissions(new HashSet<>(permissions));
        role = roleRepo.save(role);

        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll(){
        var roles = roleRepo.findAll();
        return roles.stream().map(roleMapper::toRoleResponse).toList();
    }

    public void delete(String roleId){
        System.out.println("role " + roleId);
        roleRepo.findById(roleId)
                .ifPresentOrElse(
                        roleRepo::delete,
                        () -> {
                            throw new RuntimeException("Role '" + roleId + "' không tồn tại, không thể xóa.");
                        }
                );
    }
}

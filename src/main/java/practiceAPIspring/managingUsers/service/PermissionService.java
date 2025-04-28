package practiceAPIspring.managingUsers.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practiceAPIspring.managingUsers.dto.request.permission.PermissionRequest;
import practiceAPIspring.managingUsers.dto.response.permission.PermissionResponse;
import practiceAPIspring.managingUsers.mapper.PermissionMapper;
import practiceAPIspring.managingUsers.model.Permission;
import practiceAPIspring.managingUsers.repository.PermissionRepo;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j

public class PermissionService {
    private final PermissionRepo permissionRepository;
    private final PermissionMapper permissionMapper;
    public PermissionResponse create(PermissionRequest request){
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        System.out.println("xxx1 "+permission.getRole());
        PermissionResponse as = permissionMapper.toPermissionResponse(permission);
        System.out.println("xxx2 "+as.getRole());
        return  as;
    }

    public List<PermissionResponse> getAll(){
        var permission = permissionRepository.findAll();
        return permission.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission){
        permissionRepository.deleteById(permission);
    }
}

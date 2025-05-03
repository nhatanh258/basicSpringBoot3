package practiceAPIspring.managingUsers.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import practiceAPIspring.managingUsers.dto.response.role.RoleResponse;
import practiceAPIspring.managingUsers.dto.response.user.UserResponse;
import practiceAPIspring.managingUsers.model.User;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
@Component
@AllArgsConstructor

public class UserMapper {
    private final RoleMapper roleMapper;



    public UserResponse toUserResponse(User user){// map tu user sang useresponse

//        System.out.println("==> Danh sách roles:");
//        user.getRoles().forEach(r -> System.out.println("Role: " + r.getDescription()));

        Set<RoleResponse> roleResponses = user.getRoles().stream()
                .map(role -> {
//                    System.out.println("Mapping role: " + role.getName());
//                    System.out.println("Mapping role: " + role.getDescription());
//                    System.out.println("Mapping role: " + role.getPermissions());
                    RoleResponse response = roleMapper.toRoleResponse(role);
//                    System.out.println("Response : "+ response.getName());//null-->
//                    System.out.println("Response : "+ response.getDescription());
//                    response.getPermissions().forEach(s ->
//                            System.out.println("xx " + s.getDescription()));

                    if (response == null) {
                        System.out.println("⚠️ toRoleResponse trả về null cho role: " + role.getName());
                    } else {
                        System.out.println("✅ Mapped: " + response.getName());
                    }

                    return response;
                })
                .filter(Objects::nonNull) // lọc bỏ null để tránh lỗi khi sử dụng
                .collect(Collectors.toSet());

        if(roleResponses == null){
            System.out.println("still error! ");
        }else {
            System.out.println("OK");
            roleResponses.forEach((s) ->
                    System.out.println(" xx "+s.getName()));
        }

        return UserResponse
                .builder()
                .id(String.valueOf(user.getId()))
                .fullName(user.getFullName())
                .numberId(user.getNumberId())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .roles(roleResponses)
                .creating(user.getCreatedAt())
                .updating(user.getUpdatedAt())
                .build();
    }
}

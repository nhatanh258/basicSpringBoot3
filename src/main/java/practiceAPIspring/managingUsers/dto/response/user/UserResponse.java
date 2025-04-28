package practiceAPIspring.managingUsers.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import practiceAPIspring.managingUsers.dto.response.role.RoleResponse;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String fullName;
    private String numberId;
    private String phoneNumber;
    private String email;
    private Set<RoleResponse> roles;
    private Date creating;
    private Date updating;
}

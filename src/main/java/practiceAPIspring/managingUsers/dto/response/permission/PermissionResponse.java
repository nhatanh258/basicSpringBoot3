package practiceAPIspring.managingUsers.dto.response.permission;

import lombok.*;

@AllArgsConstructor
@Builder
@Setter
@Getter
@NoArgsConstructor

public class PermissionResponse {
    private String role;
    private String description;
}
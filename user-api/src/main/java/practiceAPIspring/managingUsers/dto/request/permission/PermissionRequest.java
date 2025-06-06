package practiceAPIspring.managingUsers.dto.request.permission;


import jakarta.validation.constraints.NotBlank;
import lombok.*;



@AllArgsConstructor
@Builder
@Setter
@Getter
@NoArgsConstructor
public class PermissionRequest {
    @NotBlank(message = "Role không được để trống")
    private String role;

    @NotBlank(message = "Description không được để trống")
    private String description;
}

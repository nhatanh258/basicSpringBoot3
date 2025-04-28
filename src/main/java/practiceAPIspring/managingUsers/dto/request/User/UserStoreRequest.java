package practiceAPIspring.managingUsers.dto.request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStoreRequest {
    @NotEmpty(message = "Name is required")
    @Size(min = 10, message = "Name should have at least 10 characters")
    private String fullName;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty
    private String numberId;


    @NotEmpty
    private String phoneNumber;

    @NotEmpty
    private String password;
}

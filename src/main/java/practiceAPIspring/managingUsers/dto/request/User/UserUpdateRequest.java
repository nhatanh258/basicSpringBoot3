package practiceAPIspring.managingUsers.dto.request.User;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UserUpdateRequest {
    private String fullName;
    private String email;
    private String numberId;
    private String phoneNumber;
    private List<String> roles;

}
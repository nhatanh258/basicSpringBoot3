package practiceAPIspring.managingUsers.dto.response.comonResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class logoutRequest {
    private String token;
}

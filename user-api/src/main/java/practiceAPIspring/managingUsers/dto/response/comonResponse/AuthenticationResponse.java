package practiceAPIspring.managingUsers.dto.response.comonResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class
AuthenticationResponse {
    private String tokenAccess;
    private String tokenRefresh;
    public boolean authenticated;
}

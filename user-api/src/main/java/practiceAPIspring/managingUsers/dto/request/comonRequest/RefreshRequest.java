package practiceAPIspring.managingUsers.dto.request.comonRequest;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RefreshRequest {
    private String tokenRefresh;
}

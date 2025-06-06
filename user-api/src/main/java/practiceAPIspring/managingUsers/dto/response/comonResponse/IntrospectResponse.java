package practiceAPIspring.managingUsers.dto.response.comonResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class IntrospectResponse {
    boolean valid;
}

package practiceAPIspring.managingUsers.dto.response.comonResponse;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseObject <T>{
    private String message;
    private T data;
}

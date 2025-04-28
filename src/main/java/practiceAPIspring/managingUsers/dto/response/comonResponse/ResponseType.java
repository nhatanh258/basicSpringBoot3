package practiceAPIspring.managingUsers.dto.response.comonResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseType {
    private int code ;
    private String message;
}
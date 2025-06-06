package practiceAPIspring.managingUsers.dto.response.comonResponse;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data

public class ResponListObject<T>{
    private String message;
    private List<T> data;
}

package practiceAPIspring.managingUsers.dto.request.event;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@Builder
@Setter
@Getter
@NoArgsConstructor
public class EventRequest {
    @NotBlank(message = "ten su kien không được để trống")
    private String name;

    @NotBlank(message = "noi dung event không được để trống")
    private String description;
}

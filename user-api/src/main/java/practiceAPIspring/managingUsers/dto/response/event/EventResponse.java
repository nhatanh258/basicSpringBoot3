package practiceAPIspring.managingUsers.dto.response.event;

import lombok.*;

@AllArgsConstructor
@Builder
@Setter
@Getter
@NoArgsConstructor
public class EventResponse {
    private String name;
    private String description;
}

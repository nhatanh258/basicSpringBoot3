package practiceAPIspring.managingUsers.dto.request.email;

import java.util.Map;

public class EmailRequest {
    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> templateModel;
}

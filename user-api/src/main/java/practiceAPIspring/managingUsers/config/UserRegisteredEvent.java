package practiceAPIspring.managingUsers.config;

import org.springframework.context.ApplicationEvent;

public class UserRegisteredEvent extends ApplicationEvent {
    private final String name;
    private final String content;

    public UserRegisteredEvent(String name, String content) {
        super(name);
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }
    public String getContent() {
        return content;
    }
}


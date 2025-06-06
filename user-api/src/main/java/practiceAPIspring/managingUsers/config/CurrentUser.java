package practiceAPIspring.managingUsers.config;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import practiceAPIspring.managingUsers.model.User;


@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentUser {

    private User user;

    public void set(User user) {
        this.user = user;
    }

    public User get() {
        return user;
    }
}
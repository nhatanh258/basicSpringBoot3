package practiceAPIspring.managingUsers.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class LifeCycleUser {
    private String name;

    public LifeCycleUser() {
        System.out.println("'==================LIFE CYCLE IS RUNNING ========================== >>>>>>>>>>>>");
        System.out.println("🟢 User Bean được tạo: constructor");
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ @PostConstruct - beanUser đã sẵn sàng dùng");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("🛑 @PreDestroy - beanUser sắp bị hủy");
    }

    // Getter/setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

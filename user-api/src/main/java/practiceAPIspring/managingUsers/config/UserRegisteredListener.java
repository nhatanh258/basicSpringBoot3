package practiceAPIspring.managingUsers.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import practiceAPIspring.managingUsers.service.EmailService;


@Slf4j
@Component
public class UserRegisteredListener {

    @Autowired
    private EmailService mailService;

    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        try {
            log.info("Da lang nghe su kien tu tong dai!!!");
            log.info("Ten su kien: " + event.getName());
            log.info("Noi dung su kien: " + event.getContent());
            log.info("dang gui su kien toi anhnnfx30893@funix.edu.vn " );
            mailService.sendSimpleEmail(
                            "anhnnfx30893@funix.edu.vn",
                    event.getName(),
                    event.getContent()
            );
        } catch (Exception e) {
            System.err.println("Không gửi được email: " + e.getMessage());
        }
    }

}

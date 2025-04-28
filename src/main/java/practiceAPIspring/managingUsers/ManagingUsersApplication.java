package practiceAPIspring.managingUsers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
public class ManagingUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagingUsersApplication.class, args);
	}

}

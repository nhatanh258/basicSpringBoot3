package practiceAPIspring.managingUsers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import practiceAPIspring.managingUsers.controller.UserController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")  // Đảm bảo rằng bạn sử dụng profile 'test' cho việc tes
class ManagingUsersApplicationTests {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UserController userController;

	@Test
	void contextLoads() {
		// Kiểm tra context có tải đúng không
		assertThat(userController).isNotNull();
	}
}

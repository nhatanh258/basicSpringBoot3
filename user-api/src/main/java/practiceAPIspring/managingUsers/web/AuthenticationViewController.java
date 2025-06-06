package practiceAPIspring.managingUsers.web;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import practiceAPIspring.managingUsers.model.User;


@Controller
@AllArgsConstructor
public class AuthenticationViewController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Tìm login.html trong src/main/resources/templates
    }

    @GetMapping("/loginfail")
    public String showLoginPageFail() {
        return "loginfail"; // Tìm login.html trong src/main/resources/templates
    }

    // Nếu có trang register, forgot password,...
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User()); // or your User DTO
        return "register";
    }

}

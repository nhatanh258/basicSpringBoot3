package practiceAPIspring.managingUsers.web;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import practiceAPIspring.managingUsers.config.CustomUserDetails;
import practiceAPIspring.managingUsers.model.User;


import java.util.Locale;

@Controller
@RequestMapping
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class googleViewController {




    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {//Đảm bảo rằng đã có đối tượng xác thực khi dnag nhap
            Object principal = auth.getPrincipal();
//Lấy thông tin principal (chính là người dùng hiện tại).
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                model.addAttribute("username", userDetails.getUser().getFullName()); // hoặc getEmail()

            } else if (principal instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) principal;
                model.addAttribute("username", oAuth2User.getAttribute("name")); // hoặc "email" tùy Google trả gì

            } else {
                User user = (User) principal; // Nếu bạn có một lớp User khác
                model.addAttribute("username",user.getFullName()); // Nếu không phải là CustomUserDetails hay OAuth2User, lấy tên người dùng từ Authentication
            }

        } else {
            model.addAttribute("username", "Guest");
        }

        return "home";
    }



    @Autowired
    private MessageSource messageSource;

    @GetMapping("/welcome")
    public String welcome(Model model, Locale locale) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        String imageUrl = "";
                Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            username = userDetails.getUser().getFullName(); // hoặc getEmail()

        } else if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;
            username = oAuth2User.getAttribute("name"); // hoặc "email" tùy Google trả gì

        } else {
            User user = (User) principal; // Nếu bạn có một lớp User khác
            username = user.getFullName(); // Nếu không phải là CustomUserDetails hay OAuth2User, lấy tên người dùng từ Authentication
            imageUrl=user.getImageUrl();
        }



        System.out.println("imageUrl: " + imageUrl);

        // Dùng key 'g
        // reeting' từ file messages_vi.properties/messages_en.properties
        String message = messageSource.getMessage("greeting", new Object[]{username, imageUrl}, locale);
        System.out.println("message: " + message);
//        model.addAttribute("message", message); // nếu bạn muốn dùng message trong template
        model.addAttribute("username", username);
        model.addAttribute("avatarUrl", imageUrl);

        return "user";
    }

}

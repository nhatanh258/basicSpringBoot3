package practiceAPIspring.managingUsers.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import practiceAPIspring.managingUsers.model.Role;
import practiceAPIspring.managingUsers.model.User;
import practiceAPIspring.managingUsers.repository.RoleRepo;
import practiceAPIspring.managingUsers.repository.UserRepo;
import practiceAPIspring.managingUsers.service.AuthenticationService;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
@AllArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private AuthenticationService authenticationService;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Lấy thông tin cần thiết từ Google (email, name, etc.)
        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");
        String randomPass = passwordEncoder.encode("ranDom123");

        //luu nguoi dung vao data neu chua co
        User newUser = new User();
        String accessToken = "";
        System.out.println("Find user by email: " + userRepo.findByEmail(email));

        if(userRepo.findByEmail(email).isEmpty()){
            System.out.println("save new person !");
            newUser = User.builder()
                    .email(email)
                    .fullName(fullName)
                    .numberId(String.valueOf(new Random().nextInt()))
                    .password(randomPass)
                    .build();
            Role role = roleRepo.findByName("USER")// tra ve 1 role
                    .orElseThrow(() -> new RuntimeException("Role USER not found"));
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            newUser.setRoles(roles);
            this.userRepo.save(newUser);
            System.out.println("user have already created ! ");

            accessToken = authenticationService.generateToken(newUser);
        }else {
            System.out.println("user already exists, maybe update info if needed!");
            // nếu cần update thông tin user cũ thì làm ở đây
            accessToken = authenticationService.generateToken(userRepo.findByEmail(email).orElseThrow());
        }

        // Tạo JWT access token


        // (Tùy chọn) redirect kèm token hoặc trả JSON
        response.setContentType("application/json");
        response.getWriter().write("{\"accessToken\": \"" + accessToken + "\"}");
    }
}

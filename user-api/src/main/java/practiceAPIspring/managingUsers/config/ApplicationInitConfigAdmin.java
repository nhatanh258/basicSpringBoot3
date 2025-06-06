package practiceAPIspring.managingUsers.config;




import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import practiceAPIspring.managingUsers.model.Role;
import practiceAPIspring.managingUsers.model.User;
import practiceAPIspring.managingUsers.repository.RoleRepo;
import practiceAPIspring.managingUsers.repository.UserRepo;


import java.util.*;

@Slf4j
@Configuration
public class ApplicationInitConfigAdmin {
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder ;
    @Bean
    ApplicationRunner applicationRunner(UserRepo userRepository){
        return args ->{
            if (userRepository.findByName("admin").isEmpty()){
                User user = User.builder()
                        .fullName("admin")
                        .password(passwordEncoder.encode("admin"))
                        .email("admin@gmail.com")
                        .phoneNumber("0986667088")
                        .numberId("020324")
                        .build();
                Set<Role> Roles = new HashSet<>();
                var roleAdmin = roleRepo.findByName("ADMIN")
                        .orElseThrow(() -> new IllegalCallerException("Role ADMIN not found"));
                Roles.add(roleAdmin);
                user.setRoles(Roles);


                try {
                    userRepository.save(user);
                    log.warn("admin user has been created with default password !!");
                } catch (Exception e) {
                    log.error("Error saving user: ", e);
                }
            }
        };
    }
}

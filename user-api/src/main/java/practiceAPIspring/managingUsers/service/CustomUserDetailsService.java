package practiceAPIspring.managingUsers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import practiceAPIspring.managingUsers.config.CustomUserDetails;
import practiceAPIspring.managingUsers.model.User;
import practiceAPIspring.managingUsers.repository.UserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepository;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {// nhan username tu form login
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }
}

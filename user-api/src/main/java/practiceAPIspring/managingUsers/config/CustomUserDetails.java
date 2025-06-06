package practiceAPIspring.managingUsers.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import practiceAPIspring.managingUsers.model.User;


import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) // hoặc role.getName()
                .collect(Collectors.toSet());
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // dùng email làm username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // hoặc thêm trường isExpired nếu cần
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // hoặc thêm trường isLocked nếu cần
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true; // hoặc thêm trường isActive
    }

    public User getUser() {
        return user;
    }
}

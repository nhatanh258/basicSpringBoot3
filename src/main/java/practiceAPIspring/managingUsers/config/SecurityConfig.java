package practiceAPIspring.managingUsers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import practiceAPIspring.managingUsers.controller.AuthenticationController;
import practiceAPIspring.managingUsers.controller.OAuth2AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = { "/auth/logout","/auth/login",
            "/auth/introspect", "/api/register","/auth/refreshToken", "/api/public/**"};

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2LoginSuccessHandler;


    @Autowired
    @Lazy
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(form -> form
                        .loginProcessingUrl("/login") // ✨ Đây là URL xử lý POST login
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureUrl("/login?error=true")
                )
                .authorizeRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/api/users").hasAuthority("SCOPE_ADMIN")
                        .requestMatchers("/api/**").hasAnyAuthority("SCOPE_USER", "SCOPE_ADMIN")
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout.logoutUrl("/api/logout")
                        .permitAll());

//                .formLogin(form -> form.disable());// Tắt form login mặc định

        http
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtConfigurer ->
                                jwtConfigurer.decoder(customJwtDecoder)
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}

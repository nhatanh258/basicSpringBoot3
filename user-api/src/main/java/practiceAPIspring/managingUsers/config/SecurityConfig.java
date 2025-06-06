package practiceAPIspring.managingUsers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import practiceAPIspring.managingUsers.controller.OAuth2AuthenticationSuccessHandler;
import practiceAPIspring.managingUsers.service.CustomUserDetailsService;


@Configuration
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(PasswordEncoder passwordEncoder, CustomUserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    private final String[] PUBLIC_ENDPOINTS = { "/auth/logout","/auth/login","/register",
            "/auth/introspect", "/api/register","/auth/refreshToken","/welcome", "/user","/api/public/**"};

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2LoginSuccessHandler;



    @Autowired
    @Lazy
    private CustomJwtDecoder customJwtDecoder;

    @Autowired
    private  JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); // Tạo đối tượng Provider để xác thực username/password
        provider.setUserDetailsService(userDetailsService); // Cung cấp logic lấy thông tin User từ database (thông qua CustomUserDetailsService đã implement UserDetailsService)
        provider.setPasswordEncoder(passwordEncoder); // Thiết lập password encoder để kiểm tra mật khẩu đã mã hoá (ví dụ BCrypt)
        return provider; // Trả về Bean DaoAuthenticationProvider cho Spring quản lý
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration,//r là component chính của Spring Security để xác thực user.
                                                       DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))

                .authorizeRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/api/users").hasAuthority("SCOPE_ADMIN")
                        .requestMatchers("/api/**").hasAnyAuthority("SCOPE_USER", "SCOPE_ADMIN")
                                .requestMatchers("/home").authenticated() // Bắt buộc login
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login") // Đây là GET /login trả về trang HTML
                        .loginProcessingUrl("/login") // POST /login sẽ được Spring xử lý
                        .defaultSuccessUrl("/home", true) // Sau khi login thành công sẽ redirect tới /home
                        .failureUrl("/loginfail")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                                .loginPage("/login") // sử dụng cùng trang login
                                .successHandler(oAuth2LoginSuccessHandler)
                                .failureUrl("/login?error=true")
//                                .defaultSuccessUrl("/home", true)
//                        . // chuyển hướng sau login thành công
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
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

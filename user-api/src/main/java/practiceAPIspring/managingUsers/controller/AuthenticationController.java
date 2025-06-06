package practiceAPIspring.managingUsers.controller;


import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import practiceAPIspring.managingUsers.config.ApplicationInitConfigAdmin;
import practiceAPIspring.managingUsers.config.CurrentUser;
import practiceAPIspring.managingUsers.config.SessionScopedBean;
import practiceAPIspring.managingUsers.constant.StatusMessage;
import practiceAPIspring.managingUsers.dto.request.comonRequest.AuthenticationRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.IntrospectRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.LogoutRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.RefreshRequest;
import practiceAPIspring.managingUsers.dto.response.comonResponse.AuthenticationResponse;
import practiceAPIspring.managingUsers.dto.response.comonResponse.IntrospectResponse;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponseObject;
import practiceAPIspring.managingUsers.dto.response.comonResponse.TokenRefreshResponse;
import practiceAPIspring.managingUsers.service.AuthenticationService;
import practiceAPIspring.managingUsers.service.JwtUtils;
import reactor.core.publisher.Mono;


import java.text.ParseException;
import java.time.Duration;



@RestController
@RequestMapping
@AllArgsConstructor
@NonNull
public class AuthenticationController  {
    private  final AuthenticationService authenticationService;
    private final ApplicationInitConfigAdmin applicationInitConfigAdmin1;
    private final ApplicationInitConfigAdmin applicationInitConfigAdmin2;


    @PostMapping("/auth/login")
    ResponseObject<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return new ResponseObject<>(StatusMessage.LOGINS, authenticationService.authenticate(request)) ;
    }

    @PostMapping("/auth/introspect")
    ResponseObject<IntrospectResponse> verifyToken(@RequestBody IntrospectRequest request) {
        String status =  authenticationService.introspect(request).
                isValid() ? StatusMessage.LOGINS : StatusMessage.LOGINF;
        return new ResponseObject<>(status, null) ;
    }


    @PostMapping("/auth/refreshToken")
    ResponseObject<TokenRefreshResponse> refreshToken(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return new ResponseObject<>(StatusMessage.SUCCESS, result) ;
    }
//
//
    @PostMapping("/auth/logout")
    ResponseObject<Void> logout(@RequestBody LogoutRequest logoutRequest) throws
            ParseException, JOSEException {

        authenticationService.logout(logoutRequest);
        return new ResponseObject<>(StatusMessage.SUCCESS, null);
    }

    /// //////////////////////////////
    // kiem tra doi tuong admin
    @Autowired
    private ApplicationContext context;


    @GetMapping("/auth/checkAdmin")
    public String checkAdmin() {
//        // Kiểm tra xem bean adminConfig đã được khởi tạo hay chưa
//        if (applicationInitConfigAdmin1 == null) {
//            return "Admin config not initialized";
//        }
//        // Kiểm tra xem bean adminConfig đã được khởi tạo hay chưa
//        if (applicationInitConfigAdmin2 == null) {
//            return "Admin config not initialized";
//        }
//        // Nếu cả hai bean đều đã được khởi tạo, bạn có thể sử dụng chúng
//        // kiem tra xem hai bean co phai la mot hay khong'
//        if (applicationInitConfigAdmin1 == applicationInitConfigAdmin2) {
//            return "Both beans are the same instance";
//        } else {
//            return "Both beans are different instances";
//        }

        //hoac cach 2
//        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationInitConfigAdmin.class);

        ApplicationInitConfigAdmin bean1 = context.getBean(ApplicationInitConfigAdmin.class);
        ApplicationInitConfigAdmin bean2 = context.getBean(ApplicationInitConfigAdmin.class);



        if (bean1 == bean2) {
        return  "Both beans are the same instance";
    }else {
            return "Both beans are different instances";
        }
        }





    private final CurrentUser currentUser;


    @GetMapping("/current-user")
    public String getCurrentUser() {

        return "Current User: " + currentUser ;
    }


    //kiem tra request scope

    //kiem tra VOI SEcssion
    @Autowired
    private SessionScopedBean sessionBean;

    @GetMapping("/session/set")
    public String setUsername(@RequestParam String name) {
        sessionBean.setUsername(name);
        String result = "Saved in session: " + name+ " in " + sessionBean;
        return result;
    }

    @GetMapping("/session/get")
    public String getUsername() {
        return "Session username is: " + sessionBean.getUsername();
    }

    @Autowired
    private JwtUtils jwtUtils;
    @GetMapping("/validate")
    public String viewValidate(HttpServletRequest request) {
        // 1. Lấy Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. Kiểm tra header hợp lệ
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "Missing or invalid Authorization header";
        }

        // 3. Cắt chuỗi để lấy token
        String token = authHeader.substring(7);

        // 4. Trích xuất thông tin từ token
        String username = jwtUtils.getUsernameFromToken(token);
        if (username == null) {
            return "Token is invalid";
        }

        var expiration = jwtUtils.getExpirationDateFromToken(token);
        // Bạn có thể xử lý user/session nếu có
        return "Session username is: " + username + ", expires at: " + expiration;
    }

    // tét thử nghiệm rest template and webclient
    @GetMapping("/delay")
    public Mono<String> delay() {
        return Mono.delay(Duration.ofSeconds(3))
                .map(i -> "done by " + Thread.currentThread().getName());
    }
}

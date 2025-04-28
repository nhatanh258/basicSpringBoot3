package practiceAPIspring.managingUsers.controller;


import com.nimbusds.jose.JOSEException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practiceAPIspring.managingUsers.constant.StatusMessage;
import practiceAPIspring.managingUsers.dto.request.comonRequest.AuthenticationRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.IntrospectRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.RefreshRequest;
import practiceAPIspring.managingUsers.dto.response.comonResponse.AuthenticationResponse;
import practiceAPIspring.managingUsers.dto.response.comonResponse.IntrospectResponse;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponseObject;
import practiceAPIspring.managingUsers.dto.response.comonResponse.logoutRequest;

import practiceAPIspring.managingUsers.service.AuthenticationService;


import java.text.ParseException;


@RestController
@RequestMapping
@AllArgsConstructor
public class AuthenticationController  {
    private  final AuthenticationService authenticationService;


    @PostMapping("/login")
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
    ResponseObject<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return new ResponseObject<>(StatusMessage.SUCCESS, result) ;
    }


    @PostMapping("/auth/logout")
    ResponseObject<Void> logout(@RequestBody logoutRequest logoutRequest) throws
            ParseException, JOSEException {

        authenticationService.logout(logoutRequest);
        return new ResponseObject<>(StatusMessage.SUCCESS, null);
    }

    /// //////////////////////////////
    // cấu hiình bắt token của google sau khi dăng nhập thành công

}

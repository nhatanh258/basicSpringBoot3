package practiceAPIspring.managingUsers.service;


import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.crypto.password.PasswordEncoder;
import practiceAPIspring.managingUsers.dto.request.comonRequest.AuthenticationRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.IntrospectRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.RefreshRequest;
import practiceAPIspring.managingUsers.dto.request.comonRequest.logoutRequest;
import practiceAPIspring.managingUsers.dto.response.comonResponse.AuthenticationResponse;
import practiceAPIspring.managingUsers.dto.response.comonResponse.IntrospectResponse;
import practiceAPIspring.managingUsers.model.InvalidToken;
import practiceAPIspring.managingUsers.model.Role;
import practiceAPIspring.managingUsers.model.User;
import practiceAPIspring.managingUsers.repository.InvalidTokenRepo;
import practiceAPIspring.managingUsers.repository.UserRepo;

import java.text.ParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationTest {

    @InjectMocks
    AuthenticationService authenticationService;

    @Mock
    UserRepo userRepo;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    InvalidTokenRepo invalidTokenRepo;

    @BeforeEach
    void setUp() {
        authenticationService.SIGNER_KEY = "kDKbAhOH4ZFjq06U09hQIstsmysVZDfbphsnihPvp6B6w0JQgQUWn2ZpXS1BVMAD"; // 32 bytes
        authenticationService.VALID_DURATION = "3600";
        authenticationService.REFRESH_DURATION = "7200";
    }

    @Test
    void testAuthenticate_success() {
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertTrue(response.isAuthenticated());
        assertNotNull(response.getToken());
    }

    @Test
    void testAuthenticate_invalidPassword() {
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "wrongPass");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPass");

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void testIntrospect_validToken() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setPassword("pass");
        String token = authenticationService.generateToken(user);
        IntrospectRequest request = new IntrospectRequest(token);

        IntrospectResponse response = authenticationService.introspect(request);
        assertTrue(response.isValid());
    }

    @Test
    void testLogout_tokenIsInvalidated() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setPassword("pass");

        String token = authenticationService.generateToken(user);

        logoutRequest request = new logoutRequest(token);

        assertDoesNotThrow(() -> authenticationService.logout(request));
        verify(invalidTokenRepo, atLeastOnce()).save(any(InvalidToken.class));
    }

    @Test
    void testRefreshToken_success() throws JOSEException, ParseException {
        User user = new User();
        user.setEmail("refresh@example.com");
        user.setPassword("pass");
        String oldToken = authenticationService.generateToken(user);

        when(userRepo.findByEmail("refresh@example.com")).thenReturn(Optional.of(user));
        when(invalidTokenRepo.save(any())).thenReturn(null);

        AuthenticationResponse response = authenticationService.refreshToken(new RefreshRequest(oldToken));
        assertTrue(response.isAuthenticated());
        assertNotEquals(oldToken, response.getToken());
    }

    @Test
    void testRefreshToken_userNotFound_throwsException() {
        User user = new User();
        user.setEmail("refresh@example.com");
        user.setPassword("pass");
        String fakeToken = authenticationService.generateToken(user);
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ApplicationContextException.class,
                () -> authenticationService.refreshToken(new RefreshRequest(fakeToken)));
    }
    @Test
    void testAuthenticate_userNotFound_shouldThrow() {
        AuthenticationRequest request = new AuthenticationRequest("unknown@example.com", "password");

        when(userRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> authenticationService.authenticate(request));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testRefreshToken_userNotFound_shouldThrow() throws Exception {
        // Tạo token hợp lệ nhưng không có user tương ứng trong DB
        User dummyUser = new User();
        dummyUser.setEmail("ghost@example.com");

        String token = authenticationService.generateToken(dummyUser);
        RefreshRequest refreshRequest = new RefreshRequest(token);

        when(userRepo.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        ApplicationContextException exception = assertThrows(ApplicationContextException.class,
                () -> authenticationService.refreshToken(refreshRequest));
        assertEquals("ERROR NOT FOUND USER WITH TOKEN! ", exception.getMessage());
    }

    @Test
    void testIntrospect_invalidToken_shouldReturnFalse() {
        String invalidToken = "this.is.not.a.jwt";
        IntrospectRequest request = new IntrospectRequest(invalidToken);

        IntrospectResponse response = authenticationService.introspect(request);
        assertFalse(response.isValid());
    }

    @Test
    void testLogout_invalidToken_shouldThrow() {
        String invalidToken = "bad.token.structure";
        logoutRequest request = new logoutRequest(invalidToken);

        assertThrows(RuntimeException.class, () -> authenticationService.logout(request));
    }

    @Test
    void testVerifyToken_tokenExpired_shouldThrow() {
        // Tạo user và token hết hạn bằng cách set VALID_DURATION = -1 (đã hết hạn)
        authenticationService.VALID_DURATION = "-1";
        User user = new User();
        user.setEmail("expired@example.com");

        String expiredToken = authenticationService.generateToken(user);
        IntrospectRequest request = new IntrospectRequest(expiredToken);

        IntrospectResponse response = authenticationService.introspect(request);
        assertFalse(response.isValid());
    }

    @Test
    void testVerifyToken_tokenWasLoggedOut_shouldThrow() {
        User user = new User();
        user.setEmail("loggedout@example.com");

        String token = authenticationService.generateToken(user);
        logoutRequest request = new logoutRequest(token);
        authenticationService.logout(request);

        IntrospectRequest introspectRequest = new IntrospectRequest(token);
        IntrospectResponse response = authenticationService.introspect(introspectRequest);
        assertFalse(!response.isValid());
    }

}
